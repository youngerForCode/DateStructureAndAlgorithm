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
 * ��������ҳ����Ž⣬����û�д�����ͱ���Խ������*/

/*
 * �༶��ѹ�ռ�����ѹ������ݶ�������
 * ��ѹ��Χ��0<voltage<10k ���ö����Ʊ��룬�ռ�����������ѹϵͳ�ĵ�λ�ֱ�Ϊ���ε�V1��V2��V3��
 * @version 2.0
 * @author stephenson 2016-12-5
 * ����Ч��������ʵ��һά���κ��������ֵ����
 */

/*
 * ���Ⱦɫ���࣬�������������򣬾����ռ����ĵ�λ �Ŵ��㷨��ѡ�񣬽���ͱ������ӵĶ���
 */
class Individual {
	
	private int v1;// ��ѹ1
	private int v2;// ��ѹ2
	private int v3;// ��ѹ3

	private double I1;// ����1
	private double I2;
	private double I3;

	/*��ѹ�ı仯���з�Χ�ģ����в�������ֱ�Ӱѵ�ѹ������������������Խ�磩
	 * ����ѵ�ѹ����Сֵ��Ϊһ����̬����
	 * �ѵ�ѹ���Ա仯�ķ�Χ��Ϊ���콻��Ķ���*/
	public static int BASICV1 = 3000; 
	public static int BASICV2 = 6000; 
	public static int BASICV3 = 9000;
	public static int v1Scope =500;
	public static int v2Scope =500;
	public static int v3Scope =500;
	//Ⱦɫ�峤�ȣ�������췶Χ����������
	public static int chromeLength=bitNumber_v1scope();//�仯����512=2^9;511=111111111(�Ÿ�1)
	// �������������һ������������Ⱦɫ��
	public static int numGene = chromeLength * 3;//��Ϊ��������ѹ���Գ�3


	
	
/*	//�����õĹ��캯����ʵ������ʱ���������
	public Chromosome(int V1, int V2, int V3){
		this(V1, V2, V3, 0, 0, 0);
	}*/
	public Individual() {
		this(0, 0, 0, 0, 0, 0);
	}
	public Individual(Individual c){
		this(c.v1, c.v2, c.v3, c.I1, c.I2, c.I3);
	}
    //v1�ǵ�ѹ1
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

	/*����������ѹ�ռ�������Ӧ�ȣ�����������ռ����ܻ��յĹ�����Ϊ��Ӧ��
	 * 
	 * @param I��ÿһ���ĵ���
	 * 
	 * @return ��Ӧ��
	 */
	public double fitness() {
		/*return (v1)*I1+(v2)*I2+(v3)*I3;*/
		//return (3500-v1)*(v1-3000)+(6500-v2)*(v2-6000)+(9500-v3)*(v3-9000);
		return (3500-v1)*(v1-3000);
	}

	//��������������Ҫ�����Ķ�����λ��
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

// �Ŵ��㷨��
public class GeneAlgorithm {

	public final int EG = 100;// evolution generation ��Ⱥ�ܵĽ�������
	public int g ;// ��ǰ�Ľ�������
	private List<Individual> population;//��Ⱥ
	private final int PSIZE=50;//��Ⱥ��С
	public GeneAlgorithm() {
		g=0;
	}
	//������Ⱥ
	public void evolvePopulation(){
		if(g==0)//��һ��Ҫ������Ⱥ��С����ѹ�仯��Χ
			population=initPopulation(PSIZE, Individual.v1Scope,Individual.v2Scope, Individual.v3Scope);
			
		writeData(population.toArray(new Individual[population.size()]));
		/***************
		 * �ȴ��в���ϵͳ����
		 ****************/
		readCurrent(population);
		select(population);//ѡ������
		population=crossover_population(population);
		//System.out.println(population);
		mutate(population);
		writeData(population.toArray(new Individual[population.size()]));
		g++;
		
		System.out.println("g="+g+";"+"maxFitness="+maxFitness(population));
		//System.out.println(population);
	}

	/*��Ⱥ��ʼ��
	 * @param size ��Ⱥ��С 
	 * @param scope ÿһ���缫�ϵ�ѹ�ı仯��Χ*/
	public List<Individual> initPopulation(int size,int v1Scope,int v2Scope,int v3Scope){
		
		List<Individual> population=new LinkedList<Individual>() ;//���ݴ���list���������ļ�
        Random random1=new Random((long) Math.random());//math.random()�����������
		int voltage1,voltage2,voltage3;
		for (int i = 0; i < size; i++) {
			voltage1=random1.nextInt(v1Scope)+Individual.BASICV1;
			voltage2=random1.nextInt(v2Scope)+Individual.BASICV2;
			voltage3=random1.nextInt(v3Scope)+Individual.BASICV3;
			population.add(new Individual(voltage1, voltage2, voltage3, 0, 0, 0));
		}
		
		return population;
	}
	
	// ѡ������
	public  void select(List<Individual> population) {
		quickSort(population);// ���ȶ���Ⱥ�еĸ��尴��Ӧ�ȴӴ�С����
		// Ȼ��ȥ����1/4�ĸ��壬�����м�2/4���壬����ǰ1/4���塣
		for (int i = 0; i < population.size()/4; i++) {
			Individual c=population.get(i);
			Individual temp=new Individual(c);
			population.set(population.size()-1-i, temp);
		}
	}
	
	/*��������
	 * version 2.0
	 * @param count is group.length
	 */
	public List<Individual> crossover_population(List<Individual> population){
		List<Individual> crossedList = new ArrayList<Individual>();
		//��ȡ��Ӣ���壬������ĸ��岻���н��棬������������ֱ���Ŵ�����һ��
		Individual elitism=new Individual(population.get(0));//population�Ǿ��������˵�
		crossedList.add(elitism);//���뾫Ӣ����
		
		Random random = new Random();
		Individual individual1 = null;
		Individual individual2 = null;
		while(crossedList.size() < population.size()){
			//���ȡ������������
			int indexOne =random.nextInt(population.size());
			int indexTwo =random.nextInt(population.size());
			while (indexOne == indexTwo) {
				indexOne =random.nextInt(population.size());
				indexTwo =random.nextInt(population.size());
			}
			individual1 = population.get(indexOne);
			individual2 = population.get(indexTwo);
		
			//����
			if (similarRate(individual1, individual2) < getCriticalSimilarRate()) 
				crossedList.add(crossover(individual1, individual2));	
			else//������ƶ�̫�ߣ�����Ч��Ҳ���󣬾�ֱ���������һ���Ϳ���
				crossedList.add(individual1);
		}
		return crossedList;
	}
	/*��������
	 * */
	public  void mutate(List<Individual> population){
		//��ȡ��Ӣ���壬������ĸ��岻���н��棬������������ֱ���Ŵ�����һ��
		//�ڽ����ʱ��Ҳʹ���˾�Ӣ���壬�������ڵ�һ��
	    Individual elitism=new Individual(population.get(0));
	    
		double avgFitness=avgFitness(population);//ƽ����Ӧ��
		double maxFitness=maxFitness(population);//�����Ӧ��
		
		for (Individual chromosome : population) {//����ÿһ������
			/*����Ӧ������ʣ����Ÿ�����Ӧ��������Ӧ�仯
			 * ��Ӧ��С��ƽ����Ӧ��ʱ��ȡ���������
			 * ��Ӧ�ȴ���ƽ����Ӧ��ʱ��Ҫ����Ӧ�仯 * */
			double mutationRate=0;
			if (chromosome.fitness() < avgFitness) {
				mutationRate=Pmax;
			}else {
				mutationRate=Pmax-(Pmax-Pmin)*(chromosome.fitness()-avgFitness)/(maxFitness-avgFitness);
			}
			//�������
			//Util.random()�������һ�����ʣ��������ı�����ʴ������ͱ���
			Random random =new Random();
			
			//��ѹ1
			int shift=0;
			int tempV=chromosome.getVoltage1();//ȡ����ѹ
			/*do {
				
			} while ((tempV+shift)<Individual.BASICV1 || (tempV+shift)>(Individual.BASICV1+Individual.v1Scope));*/
			for(int i=0 ;i<Individual.chromeLength;i++){//�ɸ���仯���Ⱦ���
				if (random.nextFloat() < mutationRate){
					int gene = Math.round(random.nextFloat());//�������һ����λ����
					int bit = (tempV>>i) & 1;//ȡ����iλ
					if(gene != bit){
						if(gene == 1){//bit==0����0��Ϊ1
						   int temp = (int) Math.pow(2, i);
						   //chromosome.setVoltage1(tempV+temp);
						   shift += temp;
						}else {//gene=0 ,bit=1����1��Ϊ0
							int temp = (int) Math.pow(2, i);
							//chromosome.setVoltage1(tempV-temp);
							shift -= temp;
						}
						
					}
					
				}
			}
			chromosome.setVoltage1(tempV+shift);
	
			
				

			
			
			
			//��ѹ2
			shift = 0;
		    tempV=chromosome.getVoltage2();//ȡ����ѹ
		   /* do {
		    	
			} while ((tempV+shift)<Individual.BASICV2 || (tempV+shift)>(Individual.BASICV2+Individual.v2Scope));*/
		    for(int i=0 ;i<Individual.chromeLength;i++){//�ɸ���仯���Ⱦ���
	    		if (random.nextFloat() < mutationRate){
	    			int gene = Math.round(random.nextFloat());//�������һ����λ����
	    			int bit = (tempV>>i) & 1;//ȡ����iλ
	    			if(gene != bit){
	    				if(gene == 1){//bit==0����0��Ϊ1
	    					int temp = (int) Math.pow(2, i);
	    					shift += temp;
	    				}else {//gene=0 ,bit=1����1��Ϊ0
	    					int temp = (int) Math.pow(2, i);
	    					shift -=temp;
	    				}
	    				
	    			}
	    			
	    		}
	    	}
		    chromosome.setVoltage2(tempV+shift);
		    
			//��ѹ3
		    shift = 0;
		    tempV=chromosome.getVoltage3();//ȡ����ѹ
		    /*do {
		    	
			} while ((tempV+shift)<Individual.BASICV3 || (tempV+shift)>(Individual.BASICV3+Individual.v3Scope));*/
		    for(int i=0 ;i<Individual.chromeLength;i++){//�ɸ���仯���Ⱦ���
				if (random.nextFloat() < mutationRate){
					int gene = Math.round(random.nextFloat());//�������һ����λ����
					int bit = (tempV>>i) & 1;//ȡ����iλ
					if(gene != bit){
						if(gene == 1){//bit==0����0��Ϊ1
							int temp = (int) Math.pow(2, i);
							shift +=temp;
						}else {//gene=0 ,bit=1����1��Ϊ0
							int temp = (int) Math.pow(2, i);
							shift -= temp;
						}
						
					}
					
				}
			}
			chromosome.setVoltage3(tempV+shift);
		    
		
		}//��������������    	
		population.set(0, elitism);
	}
	
	//��ȡ��������
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
	






	
	
	
	/*��������������ƶȣ�version 3.0*/
	public double similarRate(Individual a,Individual b){
		int distance=0;
		distance += Math.abs(a.getVoltage1()-b.getVoltage1());
		distance += Math.abs(a.getVoltage2()-b.getVoltage2());
		distance += Math.abs(a.getVoltage3()-b.getVoltage3());
		int total=Individual.v1Scope+Individual.v2Scope+Individual.v3Scope;
		return 1 - distance/(double)(total);//����Խ����ʶ��ԽС
	}
	/*�ٽ����ƶ����*/
	public double getCriticalSimilarRate() {
		return (1.5 + Math.sqrt(g / (double) EG)) / 3.0;
	}

/*	//��������������ƶȣ�version 2.0
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

/*	// ��������������ƶ�,version 1.0
	public  double similarRate(Individual a, Individual b) {
		int s = 0;// ��¼���ƶ�
		int sTotal = 0;
		int m;// ����ȡ����ѹ
		int n;// ����ȡ����ѹ
		m = a.getVoltage1();
		n = b.getVoltage1();
		for (int i = 0; i < Individual.binaryLength; i++) {// ��ÿһ����ѹ�����ƶ�
			if ((m & 1) == (n & 1)) {// m&1:���m�Ķ��������λ��0�������0�����λ��1����1
				s++;// ���ƾͼ�һ
			}
			m >>= 1;// ����һλ
			n >>= 1;
		}
		sTotal += s;// ��¼��ѹ1�����Ƶ�λ��
		s = 0;
		m = a.getVoltage2();
		n = b.getVoltage2();
		for (int i = 0; i < Individual.binaryLength; i++) {// ��ÿһ����ѹ�����ƶ�
			if ((m & 1) == (n & 1)) {// m&1:���m�Ķ��������λ��0�����m&1��0�����λ��1����1
				s++;
			}
			m >>= 1;// ����һλ
			n >>= 1;
		}
		sTotal += s;// ��¼��ѹ1�����Ƶ�λ��
		s = 0;
		m = a.getVoltage3();
		n = b.getVoltage3();
		for (int i = 0; i < Individual.binaryLength; i++) {// ��ÿһ����ѹ�����ƶ�
			if ((m & 1) == (n & 1)) {// m&1:���m�Ķ��������λ��0�����m&1��0�����λ��1����1
				s++;
			} 
			m >>= 1;// ����һλ
			n >>= 1;
		}
		sTotal += s;// ��¼��ѹ1�����Ƶ�λ��

		return sTotal / (double) Individual.chromoLength;
	}
*/


	
	//�������彻��
	public Individual crossover(Individual individual1,Individual individual2){
		Individual crossed =new Individual();
		
		Random random = new Random();
		//�����ѹ1
		int individual1_v =individual1.getVoltage1();
		int individual2_v =individual2.getVoltage1();
		int result = 0;
		/*do {
		} while (result > Individual.v1Scope);//���Խ�����½������
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
			
		} while (result > Individual.v2Scope);//���Խ�����½������
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
	
	
	
	/*��������
	 * version 1.0
	 * @param count is group.length
	 */
	/*public List<Individual> crossover(List<Individual> population) {
		List<Individual> crossedList = new ArrayList<Individual>();
		//��ȡ��Ӣ���壬������ĸ��岻���н��棬������������ֱ���Ŵ�����һ��
		Individual elitism=new Individual(population.get(0));//population�Ǿ��������˵�
		crossedList.add(elitism);//���뾫Ӣ����
		
		Random random = new Random();//�������������
		int count =population.size();
		while (crossedList.size() < count) {//����
			Individual chromosome1 = null;
			Individual chromosome2 = null;
			// ���ѡ����������,ע��nextInt��������group.size()��������
			int indexOne = random.nextInt(population.size());
			int indexTwo = random.nextInt(population.size());
			
			while (indexOne==indexTwo) {
				indexOne = random.nextInt(population.size());
				indexTwo = random.nextInt(population.size());	
		    }
			chromosome1 = new Individual(population.get(indexOne));
			chromosome2 = new Individual(population.get(indexTwo));
			// ���ѡ��һ������λ��
			int crossPositon = random.nextInt(Individual.binaryLength);//�ɱ仯���Ⱦ���
			int crossnum = 0;// ����ȡ������
			// ���crossposition=5,��crossnum������Ӧ����11111
			// �����Ȱ�crossnum��ʮ���������
			for (int i = 0; i < crossPositon; i++) {
				crossnum += Math.pow(2, i);// ��2��i�η�
			}

			if (similarRate(chromosome1, chromosome2) < getCriticalSimilarRate()) {
				// ����chromosome1 ��chromosome2
				
				int temp2 = chromosome2.getVoltage1() & crossnum;// ��Ҫ����Ĳ���ȡ����
				int vCross = chromosome1.getVoltage1() >> crossPositon;// ���Ƹ�λ����
				vCross = vCross << crossPositon;// ���Ƶ�λ����
				chromosome1.setVoltage1(vCross + temp2);
				int temp1 = chromosome1.getVoltage1() & crossnum;// ��Ҫ����Ĳ���ȡ����
				vCross = chromosome2.getVoltage1() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage1(vCross + temp1);
				
				temp2 = chromosome2.getVoltage2() & crossnum;
				vCross = chromosome1.getVoltage2() >> crossPositon;// �������Ƹ�λ����
				vCross = vCross << crossPositon;// ���Ƶ�λ����
				chromosome1.setVoltage2(vCross + temp2);
				temp1 = chromosome1.getVoltage2() & crossnum;// ��Ҫ����Ĳ���ȡ����
				vCross = chromosome2.getVoltage2() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage2(vCross + temp1);

				temp2 = chromosome2.getVoltage3() & crossnum;
				vCross = chromosome1.getVoltage3() >> crossPositon;// ���Ƹ�λ����
				vCross = vCross << crossPositon;// ���Ƶ�λ����
				chromosome1.setVoltage3(vCross + temp2);
				temp1 = chromosome1.getVoltage3() & crossnum;// ��Ҫ����Ĳ���ȡ����
				vCross = chromosome2.getVoltage3() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage3(vCross + temp1);
				
			}
			crossedList.add(chromosome1);
			//crossedList.add(chromosome2);

		}
		return crossedList;
	}*/
	

	
	//��Ⱥpopulation��ƽ����Ӧ��
	public  double avgFitness(List<Individual> population){
		double totalFitness=0;
		for (Individual chromosome : population) {
			totalFitness += chromosome.fitness();
		}
		return totalFitness/population.size();
		
	}
	//��Ⱥ�������Ӧ��
	public  double maxFitness(List<Individual> population){
		double max=population.get(0).fitness();
		for (Individual chromosome : population) {
			if (chromosome.fitness()>max) {
				max=chromosome.fitness();
			}
		}
		return max;
	}

	
	//����Ⱥ�е�ÿһ���ĵ�ѹ����������ļ�
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
    /*��ȡ�����缫�ĵ�������
     * return �������� */
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
	
	
	//����ͻ��ĸ���,һ�����0.05��0.3֮�� 
	public final double Pmax=0.3;//���ı������
	public final double Pmin=0.05;//��С�ı������
	//���򽻲����һ��Ϊ0.7
	//public final double crossoverRate=0.7;
	public final double RANGE=512;//�����������
	public final double STEP_LENGTH=5;//����Ĳ���
	


	
	
	
	
	

	//����Ⱥ���������򣬰���Ӧ�Ƚ�������
    public void quickSort(List<Individual> pop){
		QSort(pop, 0, pop.size()-1);
	}
    //population[low ... high]������Ӧ�Ƚ�������
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
		//����ȡ�з�������ֵ
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
	    //����˳��
	    Individual buf = pivotKey;//�ڱ�
	    while (low < high) {
	    	while(low < high && pivotKey.fitness()>=pop.get(high).fitness())
	    		high --;
	    	pop.set(low, pop.get(high));
	    	
	    	while(low < high && pop.get(low).fitness()>=pivotKey.fitness())
	    		low ++;
	    	pop.set(high, pop.get(low));
		}
	    pop.set(low, buf);
	    return low;//��������λ��
	}
	//����Ⱥpopulation[low ... high]��ֱ�Ӳ������򣬰���Ӧ�Ƚ�������
	private void insertSort(List<Individual> population,int low ,int high){
		for (int i = low+1; i < high+1; i++) 
			if (population.get(i).fitness() > population.get(i-1).fitness()) {
				Individual temp=population.get(i);//�ڱ�
				int j;
				for ( j = i-1; j>=0 && temp.fitness() > population.get(j).fitness() ; j--)
					population.set(j+1, population.get(j));
				population.set(j+1, temp);
			}
	}
	

	
	/*********************************************
	 * ��ʱ��û���õ��ĺ���
	 * *****************************8*************/

	
	
	
	/* >>>>>>>>>>LCS��������������ַ�����������ַ���<<<<<<<<<<<
	 *����������LCS�ı��壬�������ͬ�������ϵ���ͬ�ַ�����Ҳ���Ƕ��ӵ�λ��ʼ
	 *���磺 10000��100��ͬ����������������ַ�����00������100*/
	public static int LCS(char[] a, char[] b) {
		int start;// �����ַ��������
		int end = 0;// �����ַ������յ�
		int minLength=a.length<b.length? a.length:b.length;
		int[] len = new int[minLength+1];// ����c��¼��������ƥ�����
		for (int i = 0; i < len.length; i++) //��ʼ��c
			len[i]=0;
		int count=0;//������
		int max = 0;//����ַ���λ��
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
	    //ȡ������ַ���
		start = end - len[max] + 1;
		char[] p = new char[len[max]];// ��¼��ַ���
		for (int i = start; i <= end; i++) {
			p[i - start] = b[i];
		}
		//���
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
        char[] buf = new char[32];//���ε����ݳ�����32λ
        int charPos = 32;//��������λ��
        int radix = 1 << shift;//������������ת���ɵ��Ǽ�����
        int mask = radix - 1;//ȡ���λ�ϵ���
        do {
            /*���shift=1,���Ƕ�����ת���������maskһֱΪ��1��
        	iΪ������ʱ��i & mask=1��iΪż����ʱ��Ϊ0*/
        	/*���shift==4,���ǰ˽��ơ�����mask=7
        	 * i&mask �ȼ���i%mask,ȡ7������������˵��ȡ����λ*/
            buf[--charPos] = digits[i & mask];
            i >>>= shift;//���Ƹ�ֵ����߿ճ���λ��0���
        } while (i != 0);
		
		char[] ret=new char[32-charPos];
		for (int j = 31; j >= charPos; j--) {
			ret[31-j] = buf[j];
		}
		return ret;
	}
	private static final char[] digits ={'0','1'};
	
	/*��ʱû��
	 * java����ķǻ����������Ͷ������ô��ݣ�����Ϊ�˱���������⣬����дһ��ֵ���ݺ���
	 * @param ������a��astart��aend�Ķ����Ƶ�������b��bstart��bend�Ķ���.
	 */
	public static void copyValueof(Individual[] a, int aStart, int aEnd, Individual[] b, int bStart, int bEnd) {
		if (aStart < aEnd && bStart < bEnd && aEnd - aStart == bEnd - bStart && aEnd < a.length && bEnd < b.length) {
			for (int i = 0; i <= (aEnd-aStart); i++) {
				// �½�һ��ֵ��ȶ���
				int j=aStart + i;
				int k=bStart + i;
				Individual temp = new Individual(a[j].getVoltage1(), a[j].getVoltage2(), a[j].getVoltage3(),
						a[j].getI1(), a[j].getI2(), a[j].getI3());
				b[k] = temp;
			}

		}
	}
	
	

}