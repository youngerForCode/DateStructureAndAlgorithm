import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/*version 1.0  
 * û�дﵽЧ��*/
public class GeneAlgorithm {

}

/*
 * �༶��ѹ�ռ�����ѹ������ݶ�������
 * ��ѹ��Χ��0<voltage<10k ���ö����Ʊ��룬�ռ�����������ѹϵͳ�ĵ�λ�ֱ�Ϊ���ε�V1��V2��V3��
 * @version 1.0
 * @author stephenson 2016-10-17
 */

/*
 * ���Ⱦɫ���࣬�������������򣬾����ռ����ĵ�λ �Ŵ��㷨��ѡ�񣬽���ͱ������ӵĶ���
 */
class Individual {
	/*��ѹ�ı仯���з�Χ�ģ����в�������ֱ�Ӱѵ�ѹ������������������Խ�磩
	 * ����ѵ�ѹ����Сֵ��Ϊһ����̬����
	 * �ѵ�ѹ���Ա仯�ķ�Χ��Ϊ���콻��Ķ���*/
	
	public static int BASICV1 = 3000; 
	public static int BASICV2 = 6000; 
	public static int BASICV3 = 9000;
	
	private int v1;// ��ѹ1�仯��Χ
	private int v2;// ��ѹ2�仯��Χ
	private int v3;// ��ѹ3�仯��Χ

	private double I1;// ����1
	private double I2;
	private double I3;

	// ����Ⱦɫ����볤��
	public static int chromoLength = 14 * 3;// ��ѹ���10k,�����Ƶ�����ռ14λ,��Ϊ��������ѹ���Գ�3
	// ÿ��Ⱦɫ����� ����Ŀ
	public static int CS_numGene = 10;

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
		return (v1)*I1+(v2)*I2+(v3)*I3;
	}

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
	
	@Override
	public String toString() {
		return "["+(v1)+","+(v2)+","+(v3)+","+
				I1+","+I2+","+I3+"]";
	}

}

// �Ŵ��㷨��
class GA {

	public final int EG = 100;// evolution generation ��Ⱥ�ܵĽ�������
	public int g ;// ��ǰ�Ľ�������
	private List<Individual> population;//��Ⱥ
	public GA() {
		g=0;
	}
	//������Ⱥ
	public void evolvePopulation(){
		if(g==0)//��һ��Ҫ������Ⱥ��С����ѹ�仯��Χ
			population=initPopulation(10, 500, 500, 500);
			
		readCurrent(population);
		select(population);//ѡ������
		population=crossover(population);
		mutate(population);
		writeData(population.toArray(new Individual[population.size()]));
		g++;
	}

	/*��Ⱥ��ʼ��
	 * @param size ��Ⱥ��С 
	 * @param scope ÿһ���缫�ϵ�ѹ�ı仯��Χ*/
	public List<Individual> initPopulation(int size,int v1Scope,int v2Scope,int v3Scope){
		List<Individual> population=new LinkedList<Individual>() ;//���ݴ���list���������ļ�
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
	
	//��ȡ��������
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
	
	// ѡ������
	public  void select(List<Individual> population) {
		selectSort(population);// ���ȶ���Ⱥ�еĸ��尴��Ӧ�ȴӴ�С����
		// Ȼ��ȥ����1/4�ĸ��壬�����м�2/4���壬����ǰ1/4���塣
		for (int i = 0; i < population.size()/4; i++) {
			Individual c=population.get(i);
			Individual temp=new Individual(c);
			population.set(population.size()-1-i, temp);
		}
	}

	// ����Ⱥ����ѡ�����򷨣�����Ӧ�ȴӴ�С��������
	public void selectSort(List<Individual> population) {
		int max;
		for (int i = 0; i < population.size(); i++) {
			max = i;// ����ǰ�±���Ϊ���ֵ���±�
			for (int j = i + 1; j < population.size(); j++) {
				// �ͺ�ߵļ�¼���Ƚ�
				if (population.get(j).fitness() > population.get(max).fitness()) {
					max = j;
				}
			}
			if (i != max) {// ˵���бȵ�i����¼��ģ���������
				Individual temp = population.get(i);
				population.set(i, population.get(max));
				population.set(max, temp);

			}
		}
	}

	// ��������������ƶ�
	public  double similarRate(Individual a, Individual b) {
		int s = 0;// ��¼���ƶ�
		int sTotal = 0;
		int m;// ����ȡ����ѹ
		int n;// ����ȡ����ѹ
		m = a.getVoltage1();
		n = b.getVoltage1();
		for (int i = 0; i < Individual.chromoLength / 3; i++) {// ��ÿһ����ѹ�����ƶ�
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
		for (int i = 0; i < Individual.chromoLength / 3; i++) {// ��ÿһ����ѹ�����ƶ�
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
		for (int i = 0; i < Individual.chromoLength / 3; i++) {// ��ÿһ����ѹ�����ƶ�
			if ((m & 1) == (n & 1)) {// m&1:���m�Ķ��������λ��0�����m&1��0�����λ��1����1
				s++;
			} 
			m >>= 1;// ����һλ
			n >>= 1;
		}
		sTotal += s;// ��¼��ѹ1�����Ƶ�λ��

		return sTotal / (double) Individual.chromoLength;
	}

	/*>>>>>>>>>>>�ٽ����ƶ���Ʋ�������Ҫ�޸�<<<<<<<<<<<<<*/
	public double getCriticalSimilarRate() {
		return (1 + Math.sqrt(g / (double) EG)) / 3.0;
	}

	/*
	 * �������� ����仯������200�������仯������8λ����11111111=255 �������������ѹ�仯���ȶ���200����������ٸ���
	 * 
	 * @param count is group.length
	 */

	public List<Individual> crossover(List<Individual> population) {
		List<Individual> crossedList = new ArrayList<Individual>();
		//��ȡ��Ӣ���壬������ĸ��岻���н��棬������������ֱ���Ŵ�����һ��
		Individual elitism=new Individual(population.get(0));//population�Ǿ��������˵�
		crossedList.add(elitism);//���뾫Ӣ����
		
		Random random = new Random();//�������������
		int count =population.size();
		while (crossedList.size() != count) {//����
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
			int crossPositon = random.nextInt(8);// 8λ���ɱ仯���Ⱦ���
			int crossnum = 0;// ����ȡ������
			// ���crossposition=5,��crossnum������Ӧ����11111
			// �����Ȱ�crossnum��ʮ���������
			for (int i = 0; i < crossPositon; i++) {
				crossnum += Math.pow(2, i);// ��2��i�η�
			}

			if (similarRate(chromosome1, chromosome2) < getCriticalSimilarRate()) {
				// ����chromosome1 ��chromosome2
				int temp1 = chromosome1.getVoltage1() & crossnum;// ��Ҫ����Ĳ���ȡ����
				int temp2 = chromosome2.getVoltage1() & crossnum;
				int vCross = chromosome1.getVoltage1() >> crossPositon;// ���Ƹ�λ����
				vCross = vCross << crossPositon;// ���Ƶ�λ����
				chromosome1.setVoltage1(vCross + temp2);
				vCross = chromosome2.getVoltage1() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage1(vCross + temp1);
				
				temp1 = chromosome1.getVoltage2() & crossnum;// ��Ҫ����Ĳ���ȡ����
				temp2 = chromosome2.getVoltage2() & crossnum;
				vCross = chromosome1.getVoltage2() >> crossPositon;// �������Ƹ�λ����
				vCross = vCross << crossPositon;// ���Ƶ�λ����
				chromosome1.setVoltage2(vCross + temp2);
				vCross = chromosome2.getVoltage2() >> crossPositon;
				vCross = vCross << crossPositon;
				chromosome2.setVoltage2(vCross + temp1);

				temp1 = chromosome1.getVoltage3() & crossnum;// ��Ҫ����Ĳ���ȡ����
				temp2 = chromosome2.getVoltage3() & crossnum;
				vCross = chromosome1.getVoltage3() >> crossPositon;// ���Ƹ�λ����
				vCross = vCross << crossPositon;// ���Ƶ�λ����
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
	
	/*��������
	 * ���췶Χ0-512
	 * */
	public  void mutate(List<Individual> population){
		double avgFitness=avgFitness(population);//ƽ����Ӧ��
		double maxFitness=maxFitness(population);//�����Ӧ��
		
		for (Individual chromosome : population) {//����ÿһ������
			/*����Ӧ������ʣ����Ÿ�����Ӧ��������Ӧ�仯
			 * ��Ӧ��С��ƽ����Ӧ��ʱ��ȡ���������
			 * ��Ӧ�ȴ���ƽ����Ӧ��ʱ��Ҫ����Ӧ�仯
			 * */
			double mutationRate=0;
			if (chromosome.fitness() < avgFitness) {
				mutationRate=Pmax;
			}else {
				mutationRate=Pmax-(Pmax-Pmin)*(chromosome.fitness()-avgFitness)/(maxFitness-avgFitness);
			}
			//�������
			//math.random()�������һ�����ʣ��������ı�����ʴ������ͱ���
			if (Math.random()<mutationRate) {
				Random random =new Random();
				int temp=0;
				for(int i=0 ;i<8 ;i++){//8λ���ɸ���仯���Ⱦ���
					if (random.nextFloat() < mutationRate) 
						temp += Math.pow(2, i);
				}
				int tempV=chromosome.getVoltage1();
				tempV=tempV>>8;
			    tempV=tempV<<8;
			    chromosome.setVoltage1(tempV+temp);
			    //��ѹ2
			    temp=0;
				for(int i=0 ;i<8 ;i++){//8λ���ɸ���仯���Ⱦ���
					if (random.nextFloat() < mutationRate) 
						temp += Math.pow(2, i);
				}
				tempV=chromosome.getVoltage2();
				tempV=tempV>>8;
			    tempV=tempV<<8;
			    chromosome.setVoltage2(tempV+temp);
			    //��ѹ3
			    temp=0;
				for(int i=0 ;i<8 ;i++){//8λ���ɸ���仯���Ⱦ���
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
	//��������������Ҫ�����Ķ�����λ��
	private int bitNumber(){
		
		int temp=(int) Math.floor(RANGE/STEP_LENGTH);
		int bitNum=0;
		while (Math.pow(2, bitNum)<temp) {
			bitNum ++;
		}
		return bitNum;
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
	public final double crossoverRate=0.7;
	public final double RANGE=500;//�����������
	public final double STEP_LENGTH=5;//����Ĳ���
	


	
	
	
	
	
	/* >>>>>>>>>>LCS��������������ַ�����������ַ���<<<<<<<<<<<*/
	public static int LCS(char[] a, char[] b) {
		int[] c = new int[b.length];// ����c��¼��������ƥ�����
		int start;// �����ַ��������
		int end = 0;// �����ַ������յ�
		int len = 0;// �����ַ����ĳ���
		for (int i = 0; i < a.length; i++) {// ��1��ǰ����Ƚ�
			// ��2ΪʲôҪ�Ӻ���ǰ���أ���Ϊ�˰�һά����c[]������ά����ʹ��
			// ���Ҫ��ǰ���󣬾�Ҫ��c����Ϊ��ά���飬��������Ӧ�ĵ���
			for (int j = b.length - 1; j >= 0; j--) {
				{
					if (a[i] == b[j])

					{
						if (i == 0 || j == 0) {// ����ǵ�һ�л��һ��
							c[j] = 1;// �൱�ڶ�ά����c[i][j]=1
						} else {
							c[j] = c[j - 1] + 1;
						}
					} else {
						c[j] = 0;
					}
					if (c[j] > len) {
						len = c[j];// ��¼��ַ�������
						end = j;
					}
				}

			}

		}
		start = end - len + 1;
		char[] p = new char[len];// ��¼��ַ���
		for (int i = start; i <= end; i++) {
			p[i - start] = b[i];
		}
		System.out.println(p);
		return len;
	}


}