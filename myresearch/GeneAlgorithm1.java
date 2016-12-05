import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/*version 1.0  
 * 没有达到效果*/
public class GeneAlgorithm {

}

/*
 * 多级降压收集极最佳工作（暂定三级）
 * 降压范围：0<voltage<10k 采用二进制编码，收集极的三级降压系统的电位分别为整形的V1，V2，V3。
 * @version 1.0
 * @author stephenson 2016-10-17
 */

/*
 * 这个染色体类，包含三个整形域，就是收集极的电位 遗传算法的选择，交叉和变异算子的对象
 */
class Individual {
	/*电压的变化是有范围的，所有并不合适直接把电压做交叉变异操作（容易越界）
	 * 这里把电压的最小值作为一个静态常量
	 * 把电压可以变化的范围作为变异交叉的对象*/
	
	public static int BASICV1 = 3000; 
	public static int BASICV2 = 6000; 
	public static int BASICV3 = 9000;
	
	private int v1;// 电压1变化范围
	private int v2;// 电压2变化范围
	private int v3;// 电压3变化范围

	private double I1;// 电流1
	private double I2;
	private double I3;

	// 个体染色体编码长度
	public static int chromoLength = 14 * 3;// 电压最高10k,二进制的整数占14位,因为是三级电压所以乘3
	// 每条染色体基因 总数目
	public static int CS_numGene = 10;

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
		return (v1)*I1+(v2)*I2+(v3)*I3;
	}

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
	
	@Override
	public String toString() {
		return "["+(v1)+","+(v2)+","+(v3)+","+
				I1+","+I2+","+I3+"]";
	}

}

// 遗传算法类
class GA {

	public final int EG = 100;// evolution generation 种群总的进化代数
	public int g ;// 当前的进化代数
	private List<Individual> population;//种群
	public GA() {
		g=0;
	}
	//进化种群
	public void evolvePopulation(){
		if(g==0)//第一代要设置种群大小，电压变化范围
			population=initPopulation(10, 500, 500, 500);
			
		readCurrent(population);
		select(population);//选择算子
		population=crossover(population);
		mutate(population);
		writeData(population.toArray(new Individual[population.size()]));
		g++;
	}

	/*种群初始化
	 * @param size 种群大小 
	 * @param scope 每一个电极上电压的变化范围*/
	public List<Individual> initPopulation(int size,int v1Scope,int v2Scope,int v3Scope){
		List<Individual> population=new LinkedList<Individual>() ;//先暂存在list里，在输出到文件
        Random random1=new Random(8);
		int voltage1,voltage2,voltage3;
		for (int i = 0; i < size; i++) {
			voltage1=random1.nextInt(v1Scope)+Individual.BASICV1;
			voltage2=random1.nextInt(v2Scope)+Individual.BASICV2;
			voltage3=random1.nextInt(v3Scope)+Individual.BASICV3;
			population.add(new Individual(voltage1, voltage2, voltage3, 0, 0, 0));
		}
		Individual []individuals = population.toArray(new Individual[population.size()]);
		writeData(individuals);
		return population;
	}
	
	//读取电流数据
	public void readCurrent(List<Individual> individuals){
		String []currents = readData();
		for (int i = 0; i < currents.length; i++) {
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
	
	// 选择算子
	public  void select(List<Individual> population) {
		selectSort(population);// 首先对种群中的个体按适应度从大到小排序
		// 然后去掉后1/4的个体，保留中间2/4个体，复制前1/4个体。
		for (int i = 0; i < population.size()/4; i++) {
			Individual c=population.get(i);
			Individual temp=new Individual(c);
			population.set(population.size()-1-i, temp);
		}
	}

	// 对种群做简单选择排序法，按适应度从大到小降序排列
	public void selectSort(List<Individual> population) {
		int max;
		for (int i = 0; i < population.size(); i++) {
			max = i;// 将当前下标作为最大值的下标
			for (int j = i + 1; j < population.size(); j++) {
				// 和后边的记录做比较
				if (population.get(j).fitness() > population.get(max).fitness()) {
					max = j;
				}
			}
			if (i != max) {// 说明有比第i条记录大的，交换他们
				Individual temp = population.get(i);
				population.set(i, population.get(max));
				population.set(max, temp);

			}
		}
	}

	// 求两个个体的相似度
	public  double similarRate(Individual a, Individual b) {
		int s = 0;// 记录相似度
		int sTotal = 0;
		int m;// 用来取出电压
		int n;// 用来取出电压
		m = a.getVoltage1();
		n = b.getVoltage1();
		for (int i = 0; i < Individual.chromoLength / 3; i++) {// 求每一个电压的相似度
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
		for (int i = 0; i < Individual.chromoLength / 3; i++) {// 求每一个电压的相似度
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
		for (int i = 0; i < Individual.chromoLength / 3; i++) {// 求每一个电压的相似度
			if ((m & 1) == (n & 1)) {// m&1:如果m的二进制最低位是0，结果m&1是0，最低位是1则结果1
				s++;
			} 
			m >>= 1;// 右移一位
			n >>= 1;
		}
		sTotal += s;// 记录电压1的相似的位数

		return sTotal / (double) Individual.chromoLength;
	}

	/*>>>>>>>>>>>临界相似度设计不合理，需要修改<<<<<<<<<<<<<*/
	public double getCriticalSimilarRate() {
		return (1 + Math.sqrt(g / (double) EG)) / 3.0;
	}

	/*
	 * 交叉算子 如果变化幅度是200，则最多变化二进制8位，即11111111=255 这里假设三级电压变化幅度都是200，如果不是再更改
	 * 
	 * @param count is group.length
	 */

	public List<Individual> crossover(List<Individual> population) {
		List<Individual> crossedList = new ArrayList<Individual>();
		//采取精英主义，最优秀的个体不进行交叉，保护优良基因，直接遗传个下一代
		Individual elitism=new Individual(population.get(0));//population是经过排序了的
		crossedList.add(elitism);//加入精英个体
		
		Random random = new Random();//用来产生随机数
		int count =population.size();
		while (crossedList.size() != count) {//交叉
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
			int crossPositon = random.nextInt(8);// 8位，由变化幅度决定
			int crossnum = 0;// 用来取交叉数
			// 如果crossposition=5,则crossnum二进制应该是11111
			// 这里先把crossnum的十进制算出来
			for (int i = 0; i < crossPositon; i++) {
				crossnum += Math.pow(2, i);// 求2的i次方
			}

			if (similarRate(chromosome1, chromosome2) < getCriticalSimilarRate()) {
				// 交叉chromosome1 和chromosome2
				int temp1 = chromosome1.getVoltage1() & crossnum;// 把要交叉的部分取出来
				int temp2 = chromosome2.getVoltage1() & crossnum;
				int vCross = chromosome1.getVoltage1() >> crossPositon;// 右移高位补零
				vCross = vCross << crossPositon;// 左移低位补零
				chromosome1.setVoltage1(vCross + temp2);
				vCross = chromosome2.getVoltage1() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage1(vCross + temp1);
				
				temp1 = chromosome1.getVoltage2() & crossnum;// 把要交叉的部分取出来
				temp2 = chromosome2.getVoltage2() & crossnum;
				vCross = chromosome1.getVoltage2() >> crossPositon;// 正数右移高位补零
				vCross = vCross << crossPositon;// 左移低位补零
				chromosome1.setVoltage2(vCross + temp2);
				vCross = chromosome2.getVoltage2() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage2(vCross + temp1);

				temp1 = chromosome1.getVoltage3() & crossnum;// 把要交叉的部分取出来
				temp2 = chromosome2.getVoltage3() & crossnum;
				vCross = chromosome1.getVoltage3() >> crossPositon;// 右移高位补零
				vCross = vCross << crossPositon;// 左移低位补零
				chromosome1.setVoltage3(vCross + temp2);
				vCross = chromosome2.getVoltage3() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage3(vCross + temp1);
				
			}
			crossedList.add(chromosome1);
			crossedList.add(chromosome2);

		}
		return crossedList;
	}
	
	/*变异算子
	 * 变异范围0-512
	 * */
	public  void mutate(List<Individual> population){
		double avgFitness=avgFitness(population);//平均适应度
		double maxFitness=maxFitness(population);//最大适应度
		
		for (Individual chromosome : population) {//遍历每一个个体
			/*自适应变异概率，随着个体适应度做自适应变化
			 * 适应度小于平均适应度时，取最大变异概率
			 * 适应度大于平均适应度时，要自适应变化
			 * */
			double mutationRate=0;
			if (chromosome.fitness() < avgFitness) {
				mutationRate=Pmax;
			}else {
				mutationRate=Pmax-(Pmax-Pmin)*(chromosome.fitness()-avgFitness)/(maxFitness-avgFitness);
			}
			//个体变异
			//math.random()随机产生一个概率，如果个体的变异概率大于它就变异
			if (Math.random()<mutationRate) {
				Random random =new Random();
				int temp=0;
				for(int i=0 ;i<8 ;i++){//8位，由个体变化幅度决定
					if (random.nextFloat() < mutationRate) 
						temp += Math.pow(2, i);
				}
				int tempV=chromosome.getVoltage1();
				tempV=tempV>>8;
			    tempV=tempV<<8;
			    chromosome.setVoltage1(tempV+temp);
			    //电压2
			    temp=0;
				for(int i=0 ;i<8 ;i++){//8位，由个体变化幅度决定
					if (random.nextFloat() < mutationRate) 
						temp += Math.pow(2, i);
				}
				tempV=chromosome.getVoltage2();
				tempV=tempV>>8;
			    tempV=tempV<<8;
			    chromosome.setVoltage2(tempV+temp);
			    //电压3
			    temp=0;
				for(int i=0 ;i<8 ;i++){//8位，由个体变化幅度决定
					if (random.nextFloat() < mutationRate) 
						temp += Math.pow(2, i);
				}
				tempV=chromosome.getVoltage3();
				tempV=tempV>>8;
			    tempV=tempV<<8;
			    chromosome.setVoltage3(tempV+temp);
			}
		}
	}
	
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
	//交叉变异等算子需要操作的二进制位数
	private int bitNumber(){
		
		int temp=(int) Math.floor(RANGE/STEP_LENGTH);
		int bitNum=0;
		while (Math.pow(2, bitNum)<temp) {
			bitNum ++;
		}
		return bitNum;
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
	public final double crossoverRate=0.7;
	public final double RANGE=500;//变异的最大幅度
	public final double STEP_LENGTH=5;//变异的步长
	


	
	
	
	
	
	/* >>>>>>>>>>LCS问题就是求两个字符串最长公共子字符串<<<<<<<<<<<*/
	public static int LCS(char[] a, char[] b) {
		int[] c = new int[b.length];// 矩阵c记录两个串的匹配情况
		int start;// 公共字符串的起点
		int end = 0;// 公共字符串的终点
		int len = 0;// 公共字符串的长度
		for (int i = 0; i < a.length; i++) {// 串1从前往后比较
			// 串2为什么要从后往前比呢，是为了把一维数组c[]当做二维数组使用
			// 如果要从前往后，就要把c申明为二维数组，程序做相应的调整
			for (int j = b.length - 1; j >= 0; j--) {
				{
					if (a[i] == b[j])

					{
						if (i == 0 || j == 0) {// 如果是第一行或第一列
							c[j] = 1;// 相当于二维数组c[i][j]=1
						} else {
							c[j] = c[j - 1] + 1;
						}
					} else {
						c[j] = 0;
					}
					if (c[j] > len) {
						len = c[j];// 记录最长字符串长度
						end = j;
					}
				}

			}

		}
		start = end - len + 1;
		char[] p = new char[len];// 记录最长字符串
		for (int i = start; i <= end; i++) {
			p[i - start] = b[i];
		}
		System.out.println(p);
		return len;
	}


}