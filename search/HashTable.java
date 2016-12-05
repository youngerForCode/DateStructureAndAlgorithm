import java.awt.RenderingHints.Key;

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
	public HashTable() {
		// TODO Auto-generated constructor stub
	}
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



/*public class HashTable {
	散列表结构类
	 * 采用线性试探法解决冲突，元素被删除后，存储位置也删除就会割断查找后边元素的路径
	 * 
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
	采用线性试探法解决冲突时的插入函数
	 * @param 插入关键字为the key，值为object
	 * 
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
	采用线性试探法解决冲突时,根据关键字the key查找
	 * @param thekey 是关键字
	 * @return 返回关键字的值
	 * 
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
*/