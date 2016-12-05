import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.management.openmbean.OpenDataException;
import javax.swing.text.Highlighter.Highlight;

/*version 2.0
 * 大概率能找出最优解，但是没有处理交叉和变异越界问题*/

/*
 * 多级降压收集极最佳工作（暂定三级）
 * 降压范围：0<voltage<10k 采用二进制编码，收集极的三级降压系统的电位分别为整形的V1，V2，V3。
 * @version 2.0
 * @author stephenson 2016-12-5
 * 程序效果：可以实现一维二次函数的最大值搜索
 */

/*
 * 这个染色体类，包含三个整形域，就是收集极的电位 遗传算法的选择，交叉和变异算子的对象
 */
class Individual {
	
	private int v1;// 电压1
	private int v2;// 电压2
	private int v3;// 电压3

	private double I1;// 电流1
	private double I2;
	private double I3;

	/*电压的变化是有范围的，所有并不合适直接把电压做交叉变异操作（容易越界）
	 * 这里把电压的最小值作为一个静态常量
	 * 把电压可以变化的范围作为变异交叉的对象*/
	public static int BASICV1 = 3000; 
	public static int BASICV2 = 6000; 
	public static int BASICV3 = 9000;
	public static int v1Scope =500;
	public static int v2Scope =500;
	public static int v3Scope =500;
	//染色体长度，交叉变异范围都由他决定
	public static int chromeLength=bitNumber_v1scope();//变化幅度512=2^9;511=111111111(九个1)
	// 个体基因总数，一个个体有三条染色体
	public static int numGene = chromeLength * 3;//因为是三级电压所以乘3


	
	
/*	//测试用的构造函数，实际运行时不能用这个
	public Chromosome(int V1, int V2, int V3){
		this(V1, V2, V3, 0, 0, 0);
	}*/
	public Individual() {
		this(0, 0, 0, 0, 0, 0);
	}
	public Individual(Individual c){
		this(c.v1, c.v2, c.v3, c.I1, c.I2, c.I3);
	}
    //v1是电压1
	public Individual(int V1, int V2, int V3, double I1, double I2, double I3) {
		this.v1 = V1;
		this.v2 = V2;
		this.v3 = V3;
		this.I1 = I1;
		this.I2 = I2;
		this.I3 = I3;
	}

	public void setI1(double i1) {
		I1 = i1;
	}

	public void setI2(double i2) {
		I2 = i2;
	}

	public void setI3(double i3) {
		I3 = i3;
	}

	public double getI1() {
		return I1;
	}

	public double getI2() {
		return I2;
	}

	public double getI3() {
		return I3;
	}

	public int getVoltage1() {
		return v1;
	}

	public int getVoltage2() {
		return v2;
	}

	public int getVoltage3() {
		return v3;
	}

	public void setVoltage1(int voltage1) {
		this.v1 = voltage1;
	}

	public void setVoltage2(int voltage2) {
		this.v2 = voltage2;
	}

	public void setVoltage3(int voltage3) {
		this.v3 = voltage3;
	}

	/*计算三级降压收集极的适应度，我们这里把收集极能回收的功率作为适应度
	 * 
	 * @param I是每一级的电流
	 * 
	 * @return 适应度
	 */
	public double fitness() {
		/*return (v1)*I1+(v2)*I2+(v3)*I3;*/
		//return (3500-v1)*(v1-3000)+(6500-v2)*(v2-6000)+(9500-v3)*(v3-9000);
		return (3500-v1)*(v1-3000);
	}

	//交叉变异等算子需要操作的二进制位数
	private static int bitNumber_v1scope(){
		
		//int temp=(int) Math.floor(RANGE/STEP_LENGTH);
		int temp = 0;
		int bitNum=0;
		do {
			temp += Math.pow(2, bitNum);
			bitNum ++;
		} while (temp < v1Scope);
		return bitNum;
	}
	

	@Override
	public String toString() {
		return fitness()+ "  ["+(v1)+","+(v2)+","+(v3)+","+
				I1+","+I2+","+I3+"]";
	}

}

// 遗传算法类
public class GeneAlgorithm {

	public final int EG = 100;// evolution generation 种群总的进化代数
	public int g ;// 当前的进化代数
	private List<Individual> population;//种群
	private final int PSIZE=50;//种群大小
	public GeneAlgorithm() {
		g=0;
	}
	//进化种群
	public void evolvePopulation(){
		if(g==0)//第一代要设置种群大小，电压变化范围
			population=initPopulation(PSIZE, Individual.v1Scope,Individual.v2Scope, Individual.v3Scope);
			
		writeData(population.toArray(new Individual[population.size()]));
		/***************
		 * 等待行波管系统运行
		 ****************/
		readCurrent(population);
		select(population);//选择算子
		population=crossover_population(population);
		//System.out.println(population);
		mutate(population);
		writeData(population.toArray(new Individual[population.size()]));
		g++;
		
		System.out.println("g="+g+";"+"maxFitness="+maxFitness(population));
		//System.out.println(population);
	}

	/*种群初始化
	 * @param size 种群大小 
	 * @param scope 每一个电极上电压的变化范围*/
	public List<Individual> initPopulation(int size,int v1Scope,int v2Scope,int v3Scope){
		
		List<Individual> population=new LinkedList<Individual>() ;//先暂存在list里，在输出到文件
        Random random1=new Random((long) Math.random());//math.random()是随机数种子
		int voltage1,voltage2,voltage3;
		for (int i = 0; i < size; i++) {
			voltage1=random1.nextInt(v1Scope)+Individual.BASICV1;
			voltage2=random1.nextInt(v2Scope)+Individual.BASICV2;
			voltage3=random1.nextInt(v3Scope)+Individual.BASICV3;
			population.add(new Individual(voltage1, voltage2, voltage3, 0, 0, 0));
		}
		
		return population;
	}
	
	// 选择算子
	public  void select(List<Individual> population) {
		quickSort(population);// 首先对种群中的个体按适应度从大到小排序
		// 然后去掉后1/4的个体，保留中间2/4个体，复制前1/4个体。
		for (int i = 0; i < population.size()/4; i++) {
			Individual c=population.get(i);
			Individual temp=new Individual(c);
			population.set(population.size()-1-i, temp);
		}
	}
	
	/*交叉算子
	 * version 2.0
	 * @param count is group.length
	 */
	public List<Individual> crossover_population(List<Individual> population){
		List<Individual> crossedList = new ArrayList<Individual>();
		//采取精英主义，最优秀的个体不进行交叉，保护优良基因，直接遗传个下一代
		Individual elitism=new Individual(population.get(0));//population是经过排序了的
		crossedList.add(elitism);//加入精英个体
		
		Random random = new Random();
		Individual individual1 = null;
		Individual individual2 = null;
		while(crossedList.size() < population.size()){
			//随机取出两个个体来
			int indexOne =random.nextInt(population.size());
			int indexTwo =random.nextInt(population.size());
			while (indexOne == indexTwo) {
				indexOne =random.nextInt(population.size());
				indexTwo =random.nextInt(population.size());
			}
			individual1 = population.get(indexOne);
			individual2 = population.get(indexTwo);
		
			//交叉
			if (similarRate(individual1, individual2) < getCriticalSimilarRate()) 
				crossedList.add(crossover(individual1, individual2));	
			else//如果相似度太高，交叉效果也不大，就直接添加其中一个就可以
				crossedList.add(individual1);
		}
		return crossedList;
	}
	/*变异算子
	 * */
	public  void mutate(List<Individual> population){
		//采取精英主义，最优秀的个体不进行交叉，保护优良基因，直接遗传个下一代
		//在交叉的时候也使用了精英主义，并保存在第一个
	    Individual elitism=new Individual(population.get(0));
	    
		double avgFitness=avgFitness(population);//平均适应度
		double maxFitness=maxFitness(population);//最大适应度
		
		for (Individual chromosome : population) {//遍历每一个个体
			/*自适应变异概率，随着个体适应度做自适应变化
			 * 适应度小于平均适应度时，取最大变异概率
			 * 适应度大于平均适应度时，要自适应变化 * */
			double mutationRate=0;
			if (chromosome.fitness() < avgFitness) {
				mutationRate=Pmax;
			}else {
				mutationRate=Pmax-(Pmax-Pmin)*(chromosome.fitness()-avgFitness)/(maxFitness-avgFitness);
			}
			//个体变异
			//Util.random()随机产生一个概率，如果个体的变异概率大于它就变异
			Random random =new Random();
			
			//电压1
			int shift=0;
			int tempV=chromosome.getVoltage1();//取出电压
			/*do {
				
			} while ((tempV+shift)<Individual.BASICV1 || (tempV+shift)>(Individual.BASICV1+Individual.v1Scope));*/
			for(int i=0 ;i<Individual.chromeLength;i++){//由个体变化幅度决定
				if (random.nextFloat() < mutationRate){
					int gene = Math.round(random.nextFloat());//随机产生一个等位基因
					int bit = (tempV>>i) & 1;//取出第i位
					if(gene != bit){
						if(gene == 1){//bit==0。把0置为1
						   int temp = (int) Math.pow(2, i);
						   //chromosome.setVoltage1(tempV+temp);
						   shift += temp;
						}else {//gene=0 ,bit=1。把1置为0
							int temp = (int) Math.pow(2, i);
							//chromosome.setVoltage1(tempV-temp);
							shift -= temp;
						}
						
					}
					
				}
			}
			chromosome.setVoltage1(tempV+shift);
	
			
				

			
			
			
			//电压2
			shift = 0;
		    tempV=chromosome.getVoltage2();//取出电压
		   /* do {
		    	
			} while ((tempV+shift)<Individual.BASICV2 || (tempV+shift)>(Individual.BASICV2+Individual.v2Scope));*/
		    for(int i=0 ;i<Individual.chromeLength;i++){//由个体变化幅度决定
	    		if (random.nextFloat() < mutationRate){
	    			int gene = Math.round(random.nextFloat());//随机产生一个等位基因
	    			int bit = (tempV>>i) & 1;//取出第i位
	    			if(gene != bit){
	    				if(gene == 1){//bit==0。把0置为1
	    					int temp = (int) Math.pow(2, i);
	    					shift += temp;
	    				}else {//gene=0 ,bit=1。把1置为0
	    					int temp = (int) Math.pow(2, i);
	    					shift -=temp;
	    				}
	    				
	    			}
	    			
	    		}
	    	}
		    chromosome.setVoltage2(tempV+shift);
		    
			//电压3
		    shift = 0;
		    tempV=chromosome.getVoltage3();//取出电压
		    /*do {
		    	
			} while ((tempV+shift)<Individual.BASICV3 || (tempV+shift)>(Individual.BASICV3+Individual.v3Scope));*/
		    for(int i=0 ;i<Individual.chromeLength;i++){//由个体变化幅度决定
				if (random.nextFloat() < mutationRate){
					int gene = Math.round(random.nextFloat());//随机产生一个等位基因
					int bit = (tempV>>i) & 1;//取出第i位
					if(gene != bit){
						if(gene == 1){//bit==0。把0置为1
							int temp = (int) Math.pow(2, i);
							shift +=temp;
						}else {//gene=0 ,bit=1。把1置为0
							int temp = (int) Math.pow(2, i);
							shift -= temp;
						}
						
					}
					
				}
			}
			chromosome.setVoltage3(tempV+shift);
		    
		
		}//单个个体变异完成    	
		population.set(0, elitism);
	}
	
	//读取电流数据
	public void readCurrent(List<Individual> individuals){
		String []currents = readData();
		int n=currents.length<individuals.size()? currents.length:individuals.size();
		for (int i = 0; i < n; i++) {
			String[] strings= currents[i].split(",");
			double i1= Double.parseDouble(strings[0]);
			double i2= Double.parseDouble(strings[1]);
			double i3= Double.parseDouble(strings[2]);
			Individual individual = individuals.get(i);
			individual.setI1(i1);
			individual.setI2(i2);
			individual.setI3(i3);
		}
		
	}
	






	
	
	
	/*求两个个体的相似度，version 3.0*/
	public double similarRate(Individual a,Individual b){
		int distance=0;
		distance += Math.abs(a.getVoltage1()-b.getVoltage1());
		distance += Math.abs(a.getVoltage2()-b.getVoltage2());
		distance += Math.abs(a.getVoltage3()-b.getVoltage3());
		int total=Individual.v1Scope+Individual.v2Scope+Individual.v3Scope;
		return 1 - distance/(double)(total);//距离越大相识度越小
	}
	/*临界相似度设计*/
	public double getCriticalSimilarRate() {
		return (1.5 + Math.sqrt(g / (double) EG)) / 3.0;
	}

/*	//求两个个体的相似度，version 2.0
	public double similarRate(Individual a,Individual b){
		int temp1,temp2;
		int len=0;
		char[] c1;
		char[] c2;
		
		temp1=a.getVoltage1()-Individual.BASICV1;
		temp2=b.getVoltage1()-Individual.BASICV1;
		c1=toBinaryChar(temp1);
		c2=toBinaryChar(temp2);
	    len += LCS(c1, c2);
	    
	    temp1=a.getVoltage2()-Individual.BASICV2;
	    temp2=b.getVoltage2()-Individual.BASICV2;
	    c1=toBinaryChar(temp1);
	    c2=toBinaryChar(temp2);
	    len += LCS(c1, c2);
	    
	    temp1=a.getVoltage3()-Individual.BASICV3;
	    temp2=b.getVoltage3()-Individual.BASICV3;
	    c1=toBinaryChar(temp1);
	    c2=toBinaryChar(temp2);
	    len += LCS(c1, c2);
	    
	    return len/(double)Individual.chromoLength;
	}*/

/*	// 求两个个体的相似度,version 1.0
	public  double similarRate(Individual a, Individual b) {
		int s = 0;// 记录相似度
		int sTotal = 0;
		int m;// 用来取出电压
		int n;// 用来取出电压
		m = a.getVoltage1();
		n = b.getVoltage1();
		for (int i = 0; i < Individual.binaryLength; i++) {// 求每一个电压的相似度
			if ((m & 1) == (n & 1)) {// m&1:如果m的二进制最低位是0，结果是0，最低位是1则结果1
				s++;// 相似就加一
			}
			m >>= 1;// 右移一位
			n >>= 1;
		}
		sTotal += s;// 记录电压1的相似的位数
		s = 0;
		m = a.getVoltage2();
		n = b.getVoltage2();
		for (int i = 0; i < Individual.binaryLength; i++) {// 求每一个电压的相似度
			if ((m & 1) == (n & 1)) {// m&1:如果m的二进制最低位是0，结果m&1是0，最低位是1则结果1
				s++;
			}
			m >>= 1;// 右移一位
			n >>= 1;
		}
		sTotal += s;// 记录电压1的相似的位数
		s = 0;
		m = a.getVoltage3();
		n = b.getVoltage3();
		for (int i = 0; i < Individual.binaryLength; i++) {// 求每一个电压的相似度
			if ((m & 1) == (n & 1)) {// m&1:如果m的二进制最低位是0，结果m&1是0，最低位是1则结果1
				s++;
			} 
			m >>= 1;// 右移一位
			n >>= 1;
		}
		sTotal += s;// 记录电压1的相似的位数

		return sTotal / (double) Individual.chromoLength;
	}
*/


	
	//两个个体交叉
	public Individual crossover(Individual individual1,Individual individual2){
		Individual crossed =new Individual();
		
		Random random = new Random();
		//交叉电压1
		int individual1_v =individual1.getVoltage1();
		int individual2_v =individual2.getVoltage1();
		int result = 0;
		/*do {
		} while (result > Individual.v1Scope);//如果越界重新交叉产生
*/		for (int i = 0; i < Individual.chromeLength; i++) {
			if (random.nextDouble() < 0.5) {
			   int weight = (int) Math.pow(2, i);
			   weight = (weight&individual1_v)==weight? weight:0 ; 
			   result += weight;
			}else {
			   int weight = (int) Math.pow(2, i);
			   weight = (weight&individual2_v)==weight? weight:0 ; 
			   result += weight;
			}		
		}
		crossed.setVoltage1(Individual.BASICV1 + result);
		
		individual1_v =individual1.getVoltage2();
		individual2_v =individual2.getVoltage2();
		result = 0;
/*		do {
			
		} while (result > Individual.v2Scope);//如果越界重新交叉产生
*/		for (int i = 0; i < Individual.chromeLength; i++) {
			if (random.nextDouble() < 0.5) {
				int weight = (int) Math.pow(2, i);
				weight = (weight&individual1_v)==weight? weight:0 ; 
				result += weight;
			}else {
				int weight = (int) Math.pow(2, i);
				weight = (weight&individual2_v)==weight? weight:0 ; 
				result += weight;
			}		
		}
		crossed.setVoltage2(Individual.BASICV2 + result);
		
		individual1_v =individual1.getVoltage3();
		individual2_v =individual2.getVoltage3();
		result = 0;
		/*do {
		} while (result > Individual.v3Scope);*/
		for (int i = 0; i < Individual.chromeLength; i++) {
			if (random.nextDouble() < 0.5) {
				int weight = (int) Math.pow(2, i);
				weight = (weight&individual1_v)==weight? weight:0 ; 
				result += weight;
			}else {
				int weight = (int) Math.pow(2, i);
				weight = (weight&individual2_v)==weight? weight:0 ; 
				result += weight;
			}		
		}
		crossed.setVoltage3(Individual.BASICV3 + result);
		
		return crossed;
	}
	
	
	
	/*交叉算子
	 * version 1.0
	 * @param count is group.length
	 */
	/*public List<Individual> crossover(List<Individual> population) {
		List<Individual> crossedList = new ArrayList<Individual>();
		//采取精英主义，最优秀的个体不进行交叉，保护优良基因，直接遗传个下一代
		Individual elitism=new Individual(population.get(0));//population是经过排序了的
		crossedList.add(elitism);//加入精英个体
		
		Random random = new Random();//用来产生随机数
		int count =population.size();
		while (crossedList.size() < count) {//交叉
			Individual chromosome1 = null;
			Individual chromosome2 = null;
			// 随机选择两个个体,注意nextInt的上限是group.size()，开区间
			int indexOne = random.nextInt(population.size());
			int indexTwo = random.nextInt(population.size());
			
			while (indexOne==indexTwo) {
				indexOne = random.nextInt(population.size());
				indexTwo = random.nextInt(population.size());	
		    }
			chromosome1 = new Individual(population.get(indexOne));
			chromosome2 = new Individual(population.get(indexTwo));
			// 随机选择一个交叉位置
			int crossPositon = random.nextInt(Individual.binaryLength);//由变化幅度决定
			int crossnum = 0;// 用来取交叉数
			// 如果crossposition=5,则crossnum二进制应该是11111
			// 这里先把crossnum的十进制算出来
			for (int i = 0; i < crossPositon; i++) {
				crossnum += Math.pow(2, i);// 求2的i次方
			}

			if (similarRate(chromosome1, chromosome2) < getCriticalSimilarRate()) {
				// 交叉chromosome1 和chromosome2
				
				int temp2 = chromosome2.getVoltage1() & crossnum;// 把要交叉的部分取出来
				int vCross = chromosome1.getVoltage1() >> crossPositon;// 右移高位补零
				vCross = vCross << crossPositon;// 左移低位补零
				chromosome1.setVoltage1(vCross + temp2);
				int temp1 = chromosome1.getVoltage1() & crossnum;// 把要交叉的部分取出来
				vCross = chromosome2.getVoltage1() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage1(vCross + temp1);
				
				temp2 = chromosome2.getVoltage2() & crossnum;
				vCross = chromosome1.getVoltage2() >> crossPositon;// 正数右移高位补零
				vCross = vCross << crossPositon;// 左移低位补零
				chromosome1.setVoltage2(vCross + temp2);
				temp1 = chromosome1.getVoltage2() & crossnum;// 把要交叉的部分取出来
				vCross = chromosome2.getVoltage2() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage2(vCross + temp1);

				temp2 = chromosome2.getVoltage3() & crossnum;
				vCross = chromosome1.getVoltage3() >> crossPositon;// 右移高位补零
				vCross = vCross << crossPositon;// 左移低位补零
				chromosome1.setVoltage3(vCross + temp2);
				temp1 = chromosome1.getVoltage3() & crossnum;// 把要交叉的部分取出来
				vCross = chromosome2.getVoltage3() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage3(vCross + temp1);
				
			}
			crossedList.add(chromosome1);
			//crossedList.add(chromosome2);

		}
		return crossedList;
	}*/
	

	
	//种群population的平均适应度
	public  double avgFitness(List<Individual> population){
		double totalFitness=0;
		for (Individual chromosome : population) {
			totalFitness += chromosome.fitness();
		}
		return totalFitness/population.size();
		
	}
	//种群的最大适应度
	public  double maxFitness(List<Individual> population){
		double max=population.get(0).fitness();
		for (Individual chromosome : population) {
			if (chromosome.fitness()>max) {
				max=chromosome.fitness();
			}
		}
		return max;
	}

	
	//把种群中的每一个的电压数据输出到文件
	public void writeData(Individual[] individuals){
		try {
			PrintWriter out=new PrintWriter("result.txt");
			//write the length of individuals
			out.println(individuals.length);
			for (Individual individual : individuals) {
				out.println(individual.getVoltage1()+","+individual.getVoltage2()+","+
			                                             individual.getVoltage3());
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    /*读取各个电极的电流数据
     * return 电流数组 */
	public String[] readData(){
		List<String> list=new LinkedList<String>();
		try {
			Scanner in = new Scanner(new FileReader("current.txt"));
			String line;
			while(in.hasNextLine()){
				line=in.nextLine();
				list.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list.toArray(new String[list.size()]); 
	}
	
	
	//基因突变的概率,一般介于0.05和0.3之间 
	public final double Pmax=0.3;//最大的变异概率
	public final double Pmin=0.05;//最小的变异概率
	//基因交叉概率一般为0.7
	//public final double crossoverRate=0.7;
	public final double RANGE=512;//变异的最大幅度
	public final double STEP_LENGTH=5;//变异的步长
	


	
	
	
	
	

	//对种群做快速排序，按适应度降序排列
    public void quickSort(List<Individual> pop){
		QSort(pop, 0, pop.size()-1);
	}
    //population[low ... high]，按适应度降序排列
	private void QSort(List<Individual> population,int low ,int high){
		int pivot;
		if (high - low > 7) {
			while(low < high){
				pivot = partition(population, low, high);
				QSort(population, low, pivot-1);
				low = pivot + 1;
			}
		}else
			insertSort(population,low ,high);
	}
	private int partition(List<Individual> pop,int low,int high){
		Individual pivotKey;
		//三数取中法求枢轴值
		int mid = low + (high - low)/2;
		if (pop.get(high).fitness() > pop.get(low).fitness() ) {
			Individual i=pop.get(low);
			pop.set(low, pop.get(high));
			pop.set(high, i);
		}
		if (pop.get(high).fitness() > pop.get(mid).fitness()) {
			Individual i= pop.get(mid);
			pop.set(mid, pop.get(high));
			pop.set(high, i);
		}
		if (pop.get(mid).fitness() < pop.get(low).fitness()) {
			Individual i=pop.get(low);
			pop.set(low, pop.get(mid));
			pop.set(mid, i);
		}
	    pivotKey = pop.get(low);
	    //调整顺序
	    Individual buf = pivotKey;//哨兵
	    while (low < high) {
	    	while(low < high && pivotKey.fitness()>=pop.get(high).fitness())
	    		high --;
	    	pop.set(low, pop.get(high));
	    	
	    	while(low < high && pop.get(low).fitness()>=pivotKey.fitness())
	    		low ++;
	    	pop.set(high, pop.get(low));
		}
	    pop.set(low, buf);
	    return low;//返回枢轴位置
	}
	//对种群population[low ... high]做直接插入排序，按适应度降序排列
	private void insertSort(List<Individual> population,int low ,int high){
		for (int i = low+1; i < high+1; i++) 
			if (population.get(i).fitness() > population.get(i-1).fitness()) {
				Individual temp=population.get(i);//哨兵
				int j;
				for ( j = i-1; j>=0 && temp.fitness() > population.get(j).fitness() ; j--)
					population.set(j+1, population.get(j));
				population.set(j+1, temp);
			}
	}
	

	
	/*********************************************
	 * 暂时还没有用到的函数
	 * *****************************8*************/

	
	
	
	/* >>>>>>>>>>LCS问题就是求两个字符串最长公共子字符串<<<<<<<<<<<
	 *但是这里是LCS的变体，求的是相同数量级上的相同字符串，也就是都从低位开始
	 *例如： 10000与100的同数量级上最长公共子字符串是00而不是100*/
	public static int LCS(char[] a, char[] b) {
		int start;// 公共字符串的起点
		int end = 0;// 公共字符串的终点
		int minLength=a.length<b.length? a.length:b.length;
		int[] len = new int[minLength+1];// 矩阵c记录两个串的匹配情况
		for (int i = 0; i < len.length; i++) //初始化c
			len[i]=0;
		int count=0;//计数器
		int max = 0;//最长子字符串位置
		for (int i = 0; i < minLength; i++) {
					if (a[i] == b[i])
					{
						len[count]++;
					}else
						count++;
					if (len[count] >= len[max]) {
						max = count;
						end =i;
					}
		}
	    //取出最长子字符串
		start = end - len[max] + 1;
		char[] p = new char[len[max]];// 记录最长字符串
		for (int i = start; i <= end; i++) {
			p[i - start] = b[i];
		}
		//输出
/*		for (int i = p.length-1; i >=0; i--) {
			System.out.print(p[i]);
		}
		System.out.println();*/
		return len[max];
	}
	
	public char[] toBinaryChar(int i){
		return toUnsignedChar(i, 1);
	}
	private char[] toUnsignedChar(int i,int shift){
        char[] buf = new char[32];//整形的数据长度是32位
        int charPos = 32;//控制数组位子
        int radix = 1 << shift;//基数，决定了转化成的是几进制
        int mask = radix - 1;//取最低位上的树
        do {
            /*如果shift=1,就是二进制转换。这里的mask一直为：1。
        	i为奇数的时候，i & mask=1，i为偶数的时候为0*/
        	/*如果shift==4,就是八进制。这里mask=7
        	 * i&mask 等价于i%mask,取7的余数。或者说是取低三位*/
            buf[--charPos] = digits[i & mask];
            i >>>= shift;//右移赋值，左边空出的位以0填充
        } while (i != 0);
		
		char[] ret=new char[32-charPos];
		for (int j = 31; j >= charPos; j--) {
			ret[31-j] = buf[j];
		}
		return ret;
	}
	private static final char[] digits ={'0','1'};
	
	/*暂时没用
	 * java里面的非基本数据类型都是引用传递，所以为了避免别名问题，必须写一个值传递函数
	 * @param 将数组a从astart到aend的对象复制到，数组b的bstart到bend的对象.
	 */
	public static void copyValueof(Individual[] a, int aStart, int aEnd, Individual[] b, int bStart, int bEnd) {
		if (aStart < aEnd && bStart < bEnd && aEnd - aStart == bEnd - bStart && aEnd < a.length && bEnd < b.length) {
			for (int i = 0; i <= (aEnd-aStart); i++) {
				// 新建一个值相等对象
				int j=aStart + i;
				int k=bStart + i;
				Individual temp = new Individual(a[j].getVoltage1(), a[j].getVoltage2(), a[j].getVoltage3(),
						a[j].getI1(), a[j].getI2(), a[j].getI3());
				b[k] = temp;
			}

		}
	}
	
	

}