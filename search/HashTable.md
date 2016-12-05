###概述
散列技术： 存储位置=f(key) 
在记录的存储位置和它的关键字之间建立一种确定的关系F，使得每一个关键字key都有一个存储位置F(key)。 
这个关系f称为散列函数，又称为哈希（hash）函数。 
散列表： 
采用散列技术将记录存储在一块连续的存储空间中，这块连续的存储空间就称为散列表或者哈希表（hash table）。
***
###散列表查找步骤
1. 在存储时，通过散列函数计算散列地址，并在散列地址存储记录。
2. 查找记录时，通过同样的散列函数计算散列地址，并按散列地址访问该记录。
所以，散列技术即是一种存储方法，也是一种查找方法。
***
###散列表的特点
1.	散列技术最适合求解问题是查找与给定值相等的记录。
2.	散列技术的记录之间没有什么逻辑关系。
3.	同样的关键字，能对应很多个记录的情况不适合散列技术
4.	表中的记录不能排序，像最大值、最小值等结果也无法从散列表中计算出来
5.	经常会有冲突现象 
note ：冲突（collision）：两个关键字key1 != key2，却有f(key1) = f(key2)，这种现象为冲突。把key1 和key2 称为这个函数的同义词（synonym）。 
如何处理冲突时一个很重要的课题。
***
###散列函数的构造方法
一个好的散列函数的，两个常见原则。 
1. 计算简单。（散列函数的计算时间不应该超过其他查找技术与关键字比较的时间） 
2. 散列地址分配均匀。（保证存储空间的有效利用，减少处理冲突而耗费时间）

####直接定址法
取关键字的某个线性函数作为散列函数 ，即f(key) = a*key +b。 
优点： 
　　简单，均匀，也不会产生冲突。 
缺点： 
　　 需要事先知道关键字的分布，适合查找表较小且连续的情况。
note：这个方法并不常用
####数字分析法
　　抽取关键字的一部分来作为散列存储位置，或者抽取出来后在翻转，反转，移位，叠加。总之就是为了让关键字均匀的分布到散列表中。
　　这种抽取的方法是散列函数中常用的方法。
　　特点：
　　　适合处理关键字比较大的情况，如果事先知道关键字本身的结构分布，且关键字的若干位分布比较均匀，就可以考虑这个方法。
####平方取中法
　　先对关键字平方，在取其中间部分。
　　特点：
　　适合不知道关键字分布，为位数又不是很大的情况。
####折叠法
　　将关键字从左到右分割成位数相等的几部分（最后一个位数不够可以短一些）然后这几部分求和，并按散列表表长，去最后几位作为散列地址。
　　特点：
　　事先不知道关键字的分布，适合关键字位数较多的情况。
####除留余数法
　　最常见的散列构造函数，对于散列表长为m的散列函数为
　　                     f(key) = key mod p (p<=m)
　　note: 根据经验，若散列表长为m，通常p为小于或等于表长（最好是接近m）的最小质数，或者不包括小于20的质因子的合数。
####随机数法
　　取关键字的随机函数值作为它的散列地址。也就是f(key) = random(key)。random是随机函数。
　　特点：
　　当关键字的长度不等时，采用这个方法构造散列函数是比较合适的。
note：如果关键字是字符串，就转换为某种数字来对待，比如ASCII或者unicode码等。

####总结
实际运用中，根据不同情况采取不同的散列函数，这里有一因数来提供综合考虑。
1. 计算散列地址所需时间
2. 关键字的长度
3. 散列表的大小
4. 关键字的分布情况
5. 记录查找的频率。
***
　　
###处理散列冲突的方法
冲突有时候很难避免，所以要研究如何处理。
实在追不到梦中情人就换一个呗——开放地址法，或者去别的地方再找—再散列函数法。
####开放地址法
　　开放地址法：一旦发生冲突，就去寻找下一个空的散列地址，只要散列表足够大，总可以找到空的地址，并将记录存进去。
　　它的公式：f(key) = (f(key)+d) mod m (d = 1,2,3 ,,,m-1)
　　f(key) 是除留余数法，折叠法等。
　　这种解决冲突的开放定址法也称为线性探测法。
堆积：
　　本来不是一个同义词却要争夺一个地址的现象。
　　二次试探法：
f(key)=(f(key)+d) mod m(d = 1^2,-1^2,2^2,-2^2, .. ,q^2,-q^2;q<=m/2)
　　增加平方算子的目的是为了不让关键字都聚集在某一块区域。
　　随机试探法：
　　在冲突时，如果位移量d采用随机数函数计算得到，称之为随机试探法。当然这里的随机函数是伪随机。在查找时设置相同的随机种子，不断调用随机函数就可以生成同一个不重复的数列。
　　这是解决冲突的常用方法
####再散列函数法
　　冲突时重新选择散列函数
　　f(key)  = RHi(key) (i=1,2,3 ... ,k)
　　RHi就是不同的散列函数，总有一个函数可以把冲突解决
　　特点：
　　关键字不产生聚集，当然，相应的也增加了计算时间。
####链地址法
　　冲突也不换地方
　　将所有关键字为同义词的记录存储在一个单链表中，我们称这种表为同义词子表。在散列表中值存储所有同义词字表的的头指针。
　　特点：
　　绝对不会出现找不到地址，当然也带来了查找时需要遍历单链表的性能损耗。
####公共溢出区
　　我们为所以冲突的关键字建立一个公共的溢出区来存放记录。
　　特点：
　　在冲突数据较少的情况下，公用溢出区的结构对查找性能还是非常高的。
***
###java程序实现
####采用线性试探法解决冲突的散列查找实现
```
	/*****************************
	 * 散列表查找技术
	 * ****************************/

	/*散列表结构类
	 * 采用线性试探法解决冲突，元素被删除后，存储位置也删除就会割断查找后边元素的路径
	 * */
public class HashTable {
	private Object [] key;//b保存元素关键字数组
	private Object [] ht;//散列表
	private int count;//散列表中元素个数
	private int capacity;//散列表的容积
	private Object tag=-1;//元素内容被删除后的标记
	
	public HashTable(int capacity) {
		this.capacity=capacity;
		this.count=0;
		this.key=new Object[capacity];
		this.ht=new Object[capacity];
	}
	//散列函数
	private int hash(Object key){
		return (Integer) key % capacity;
	}
	/*采用线性试探法解决冲突时的插入函数
	 * @param 插入关键字为the key，值为object
	 * */
	public boolean insert(Object theKey,Object object){
		int addr=hash(theKey);//存储位置
		int temp=addr;
		//检查是否发生冲突
		while(key[addr] != null && !key[addr].equals(tag)){
			if(key[addr].equals(theKey))//已经有这个关键字
				break;
			addr=(addr+1)%capacity;//地址被占用，线性试探法处理冲突
			if (addr==temp) {//查找一周后任然没有位置，应该重组散列或退出
				System.out.println("散列表无空间,退出运行");
				System.exit(1);
			}
		}
		//改地址为空，或者改地址的元素已经被删除
		if (key[addr]==null || key[addr].equals(tag)) {
			key[addr]= theKey;//保存键
			ht[addr]= object;//保存值
			count++;
			return true;
		}else {//如果在满足前面关键字相等且地址也不为空，那修改值，返回false
			ht[addr] =object;
			return false;
		}
	}
	/*采用线性试探法解决冲突时,根据关键字the key查找
	 * @param thekey 是关键字
	 * @return 返回关键字的值
	 * */
	public Object search(Object theKey){
		int addr = hash(theKey);//存储地址
		int temp=addr;
		while(key[addr]!=null){//地址中是否有元素
			if(key[addr].equals(theKey))//没有冲突
				return ht[addr];
			else
				addr=(addr+1) % capacity;//线性试探法解决冲突
			if(addr == temp) return null;//循环一周之后如果任然失败
		}
		return null;
	}
	
	public boolean delete(Object theKey){
		int addr= hash(theKey);//散列地址
		int temp= addr;
		while(key[addr]!=null){//该地址是否有元素
			if (key[addr].equals(theKey)) {//查找要删除的元素
				key[addr]=tag;//设置删除标记
				ht[addr]=null;//删除对应值
				count--;
				return true;
			}
			else 
				addr=(addr+1)%capacity;//线性试探法解决冲突
			if(addr==temp) //查找一周后任然没有找到
				return false;
		}
		return false;
	}
	public void output(){
		for(int i=0;i<capacity;i++){
			if(key[i]== null || key[i].equals(tag))
				continue;
			System.out.print("("+key[i]+"  "+ht[i]+")");
		}
		System.out.println();
	}
}

```
验证小程序
```
		int []key ={18,75,60,43,54,90,46,31,58,73,15,34};//关键字数组
		String []values={"stephen","willim","carl","cristiano","jury","jobs",
				"elon","zakeberge","walker","korbe","bill","jorden"};//元素数组
		HashTable hashTable=new HashTable(12);
		for (int i = 0; i < key.length; i++) {
			hashTable.insert(key[i], values[i]);
		}
		hashTable.output();
		for(int i=0;i<key.length;i+=2)
			hashTable.delete(key[i]);
		hashTable.output();
		hashTable.insert(88, "007");
		hashTable.insert(75, "mylove");
		hashTable.output();
```
执行结果：
(60  carl)(58  walker)(73  korbe)(75  willim)(15  bill)(34  jorden)(18  stephen)(43  cristiano)(54  jury)(90  jobs)(46  elon)(31  zakeberge)
(73  korbe)(75  willim)(34  jorden)(43  cristiano)(90  jobs)(31  zakeberge)
(73  korbe)(75  mylove)(88  007)(34  jorden)(43  cristiano)(90  jobs)(31  zakeberge)

****
####采用链地址法解决冲突的散列表查找实现　　
　　
```
/*****************************
	 * 散列表查找技术
	 * ****************************/


	//采用链接法解决冲突
	//同义词结点被链接在同一个散列地址上
public class HashTable{
    //链接法存储中的结点类型
	class HashNode{
		Object key;
		Object value;
		HashNode next;
		public HashNode(Object key,Object value) {
			this.key=key;
			this.value=value;
			this.next=null;
		}
	}
	
	private int capacity;//散列表容积
	private int count;//散列表中已有的元素个数
	private HashNode [] ht;//散列表数组
	public HashTable(int capacity) {
		this.capacity=capacity;
		this.count=0;
		this.ht=new HashNode[capacity];
	}
	//散列函数
	private int hash(Object theKey){
		//散列函数用除留余数法
		return (Integer)theKey % capacity;
	}
	
	/*采用链式法处理冲突，对应的插入函数
	 * @return 插入成功返回true，修改关键字对应的值返回false
	 * */
	public boolean insert(Object theKey,Object value){
		int addr=hash(theKey);//用散列函数求地址
		HashNode p=ht[addr];//指向表头
		while(p!=null){//是否已经有元素
			if(p.key.equals(theKey))//已经存在该关键字
				break;
			else  p=p.next;
		}
		if(p!=null){//这就是已经存在该关键字时
			p.value=value;//只修改对应的值
			return false;
		}else {
			p=new HashNode(theKey, value);
			p.next=ht[addr];//指向表头
			ht[addr]=p;//替换表头
			count++;
			return true;
		}
	}
	/*采用链地址法处理冲突，对应的查找函数
	 * @return 查找成功返回对应值，查找失败返回null*/
	public Object search(Object theKey){
		int addr=hash(theKey);//散列地址
		HashNode p=ht[addr];//指向表头
		while(p!=null){
			if(p.key.equals(theKey))
				return p.value;
			else p=p.next;
		}
		return null;
	}
	/*采用链地址法处理冲突，对应的删除函数
	 * @return 删除成功返回true，查找失败返false*/
	public boolean delete(Object theKey){
		int addr=hash(theKey);//散列地址
		HashNode p=ht[addr];//指向表头
		HashNode last=null;
		while(p!=null){
			if(p.key.equals(theKey))//查找成功
				break;
			else {
				last=p;//保存上一个结点
				p=p.next;//指向下一个结点
			}
		}
		if(p==null) //没有要删除的元素
			return false;
		else if(last==null) //删除的是表头
		    ht[addr]=p.next;
		else
			last.next=p.next;//删除的是非表头结点
		count--;
		return true;
	}
	public void output(){
		for (int i = 0; i < capacity; i++) {
			HashNode p =ht[i];
			while(p!=null){
				System.out.print("("+p.key+" "+p.value+"),");
				p=p.next;
			}
		}
		System.out.println();
	}
	
}

```
验证小程序
```
		int []key ={18,75,60,43,54,90,46,31,58,73,15,34};//关键字数组
		String []values={"stephen","willim","carl","cristiano","jury","jobs",
				"elon","zakeberge","walker","korbe","bill","jorden"};//元素数组
		HashTable hashTable=new HashTable(12);
		for (int i = 0; i < key.length; i++) {
			hashTable.insert(key[i], values[i]);
		}
		hashTable.output();
		for(int i=0;i<key.length;i+=2)
			hashTable.delete(key[i]);
		hashTable.output();
		hashTable.insert(88, "007");
		hashTable.insert(75, "mylove");
		hashTable.output();
```
执行结果：
(60 carl),(73 korbe),(15 bill),(75 willim),(90 jobs),(54 jury),(18 stephen),(31 zakeberge),(43 cristiano),(34 jorden),(58 walker),(46 elon),
(73 korbe),(75 willim),(90 jobs),(31 zakeberge),(43 cristiano),(34 jorden),
(73 korbe),(75 mylove),(88 007),(90 jobs),(31 zakeberge),(43 cristiano),(34 jorden),
