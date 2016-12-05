import java.awt.RenderingHints.Key;

/*****************************
	 * ɢ�б���Ҽ���
	 * ****************************/


	//�������ӷ������ͻ
	//ͬ��ʽ�㱻������ͬһ��ɢ�е�ַ��
public class HashTable{
    //���ӷ��洢�еĽ������
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
	
	private int capacity;//ɢ�б��ݻ�
	private int count;//ɢ�б������е�Ԫ�ظ���
	private HashNode [] ht;//ɢ�б�����
	public HashTable() {
		// TODO Auto-generated constructor stub
	}
	public HashTable(int capacity) {
		this.capacity=capacity;
		this.count=0;
		this.ht=new HashNode[capacity];
	}
	//ɢ�к���
	private int hash(Object theKey){
		//ɢ�к����ó���������
		return (Integer)theKey % capacity;
	}
	
	/*������ʽ�������ͻ����Ӧ�Ĳ��뺯��
	 * @return ����ɹ�����true���޸Ĺؼ��ֶ�Ӧ��ֵ����false
	 * */
	public boolean insert(Object theKey,Object value){
		int addr=hash(theKey);//��ɢ�к������ַ
		HashNode p=ht[addr];//ָ���ͷ
		while(p!=null){//�Ƿ��Ѿ���Ԫ��
			if(p.key.equals(theKey))//�Ѿ����ڸùؼ���
				break;
			else  p=p.next;
		}
		if(p!=null){//������Ѿ����ڸùؼ���ʱ
			p.value=value;//ֻ�޸Ķ�Ӧ��ֵ
			return false;
		}else {
			p=new HashNode(theKey, value);
			p.next=ht[addr];//ָ���ͷ
			ht[addr]=p;//�滻��ͷ
			count++;
			return true;
		}
	}
	/*��������ַ�������ͻ����Ӧ�Ĳ��Һ���
	 * @return ���ҳɹ����ض�Ӧֵ������ʧ�ܷ���null*/
	public Object search(Object theKey){
		int addr=hash(theKey);//ɢ�е�ַ
		HashNode p=ht[addr];//ָ���ͷ
		while(p!=null){
			if(p.key.equals(theKey))
				return p.value;
			else p=p.next;
		}
		return null;
	}
	/*��������ַ�������ͻ����Ӧ��ɾ������
	 * @return ɾ���ɹ�����true������ʧ�ܷ�false*/
	public boolean delete(Object theKey){
		int addr=hash(theKey);//ɢ�е�ַ
		HashNode p=ht[addr];//ָ���ͷ
		HashNode last=null;
		while(p!=null){
			if(p.key.equals(theKey))//���ҳɹ�
				break;
			else {
				last=p;//������һ�����
				p=p.next;//ָ����һ�����
			}
		}
		if(p==null) //û��Ҫɾ����Ԫ��
			return false;
		else if(last==null) //ɾ�����Ǳ�ͷ
		    ht[addr]=p.next;
		else
			last.next=p.next;//ɾ�����ǷǱ�ͷ���
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
	ɢ�б�ṹ��
	 * ����������̽�������ͻ��Ԫ�ر�ɾ���󣬴洢λ��Ҳɾ���ͻ��ϲ��Һ��Ԫ�ص�·��
	 * 
	private Object [] key;//b����Ԫ�عؼ�������
	private Object [] ht;//ɢ�б�
	private int count;//ɢ�б���Ԫ�ظ���
	private int capacity;//ɢ�б���ݻ�
	private Object tag=-1;//Ԫ�����ݱ�ɾ����ı��
	
	public HashTable(int capacity) {
		this.capacity=capacity;
		this.count=0;
		this.key=new Object[capacity];
		this.ht=new Object[capacity];
	}
	//ɢ�к���
	private int hash(Object key){
		return (Integer) key % capacity;
	}
	����������̽�������ͻʱ�Ĳ��뺯��
	 * @param ����ؼ���Ϊthe key��ֵΪobject
	 * 
	public boolean insert(Object theKey,Object object){
		int addr=hash(theKey);//�洢λ��
		int temp=addr;
		//����Ƿ�����ͻ
		while(key[addr] != null && !key[addr].equals(tag)){
			if(key[addr].equals(theKey))//�Ѿ�������ؼ���
				break;
			addr=(addr+1)%capacity;//��ַ��ռ�ã�������̽�������ͻ
			if (addr==temp) {//����һ�ܺ���Ȼû��λ�ã�Ӧ������ɢ�л��˳�
				System.out.println("ɢ�б��޿ռ�,�˳�����");
				System.exit(1);
			}
		}
		//�ĵ�ַΪ�գ����߸ĵ�ַ��Ԫ���Ѿ���ɾ��
		if (key[addr]==null || key[addr].equals(tag)) {
			key[addr]= theKey;//�����
			ht[addr]= object;//����ֵ
			count++;
			return true;
		}else {//���������ǰ��ؼ�������ҵ�ַҲ��Ϊ�գ����޸�ֵ������false
			ht[addr] =object;
			return false;
		}
	}
	����������̽�������ͻʱ,���ݹؼ���the key����
	 * @param thekey �ǹؼ���
	 * @return ���عؼ��ֵ�ֵ
	 * 
	public Object search(Object theKey){
		int addr = hash(theKey);//�洢��ַ
		int temp=addr;
		while(key[addr]!=null){//��ַ���Ƿ���Ԫ��
			if(key[addr].equals(theKey))//û�г�ͻ
				return ht[addr];
			else
				addr=(addr+1) % capacity;//������̽�������ͻ
			if(addr == temp) return null;//ѭ��һ��֮�������Ȼʧ��
		}
		return null;
	}
	
	public boolean delete(Object theKey){
		int addr= hash(theKey);//ɢ�е�ַ
		int temp= addr;
		while(key[addr]!=null){//�õ�ַ�Ƿ���Ԫ��
			if (key[addr].equals(theKey)) {//����Ҫɾ����Ԫ��
				key[addr]=tag;//����ɾ�����
				ht[addr]=null;//ɾ����Ӧֵ
				count--;
				return true;
			}
			else 
				addr=(addr+1)%capacity;//������̽�������ͻ
			if(addr==temp) //����һ�ܺ���Ȼû���ҵ�
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