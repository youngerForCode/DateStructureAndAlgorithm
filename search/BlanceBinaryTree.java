import java.util.Stack;



/***************************
 * ����ƽ����
 * ****************************/
public class BlanceBinaryTree<E> {
	private BiTNode<E> root;
	private int size;
	
	public BlanceBinaryTree() {
		this.root=null;
		this.size=0;
	}
	public BlanceBinaryTree(BiTNode<E> root) {
		this.root=root;
		this.size=0;
	}
	public BlanceBinaryTree(E []values){
		for (E value : values) {
			insert(value);
		}
		
	}
	public int getSize(){
		return size;
	}
	public BiTNode<E> getRoot(){
		return root;
	}
	public boolean Constraints(E value){
		return ( getNode(value) != null );
	}
	public BiTNode<E> minValue(BiTNode<E> root){
		BiTNode<E> t=root;
		while (t.leftChild!=null) 
			t=t.leftChild;
		return t;
	}
	public BiTNode<E> maxValue(BiTNode<E> root){
		BiTNode<E> t=root;
		while (t.rightChild!=null) 
			t=t.rightChild;
		return t;
	}
	/*����ֵΪvalue�Ľ��
	 * @param value
	 * @return ֵΪvalue�Ľ��*/
	@SuppressWarnings("unchecked")
	public BiTNode<E> getNode(E value){
		BiTNode<E> t=root;
		Comparable<E> val=(Comparable<E>) value;
		while(t!=null){
			int cmp=val.compareTo(t.value);
			if(cmp<0)
				t=t.leftChild;
			else if(cmp>0)
				t=t.rightChild;
			else
				return t;
		}
		return null;
	}
	
	/*����pΪ���Ķ���������������
	 * ����֮��pָ���µ�������㣬����ת֮ǰ�������ĸ����
	 *           P                      C
	 *          / \                     /\
	 *         C   D                   E  P
	 *        / \        ����->        /  / \
	 *       E   F                   G   F  D 
	 *      /
	 *     G
	 *           P                      C
	 *          /                      / \
	 *         C                      E   P
	 *        /         ����->        
	 *       E                              
	 *      
	 *      	     
	 *     
	 * A����С��ƽ�������ĸ����    
	 * */
	private void rotateRight(BiTNode<E> p){
		if (p!=null) {
			BiTNode<E> L = p.leftChild;//Lָ��p��������
			p.leftChild=L.rightChild;//L���������ҽ���p��������
			if(L.rightChild!=null)//�ǲ��ǲ����ж�
				L.rightChild.parent=p;//���F��˫��ָ��P
			L.parent=p.parent;//C��˫��ָ��P��˫��
            //˫�ױ仯			
			if(p.parent==null)//���p��ͷ���
				root=L;
			else if (p.parent.leftChild==p)//p��˫�׵������� 
				p.parent.leftChild=L;
			else//p��˫�׵�������
				p.parent.rightChild=L;
			L.rightChild=p;//����������p��ΪL��������
			p.parent=L;
		}
		
		
		
	}
	/*����PΪ�����Ķ���������������
	 * ����֮��pָ���µĸ���㣬����ת֮ǰ�������ĸ����
	 *           P                       D
	 *          / \                     / \
	 *         C   D                   P   F
	 *            / \        ����->    / \   \
	 *           E   F               C   E   G 
	 *                \
	 *                 G
	 * A����С��ƽ�������ĸ����                                                                                      
	 * */
	private void rotateLeft(BiTNode<E> p){
		BiTNode<E> R;
		R=p.rightChild;//Rָ���������ĸ����
		p.rightChild=R.leftChild;//��R������������p����������
		//˫�ױ任
		if(R.leftChild!=null)
			R.leftChild.parent=p;//���E��˫��ָ��P
		R.parent=p.parent;//D��˫��ָ��P��˫��
		if(p.parent==null)//P�Ǹ����
			root=R;
		else if (p.parent.leftChild==p)//p������˫�׵�������
			p.parent.leftChild=R;
		else
			p.parent.rightChild=R;//P������˫�׵�������
		R.leftChild= p;//����������R��������ָ��p
		p.parent=R;	
	}
	/*������ƽ�����������С��ƽ�������ĸ������root����ƽ�����Ӵ���1
	 * 1��root����������ƽ������Ϊ������ֱ������
	 * 2��root����������ƽ������Ϊ������������������
	 * 3��root����������ƽ������Ϊ0����ֱ������
	 * 
	 *���һ                A                      C
	 *          / \                     /\
	 *         C   D                   E  A
	 *        / \        ����->        /  / \
	 *       E   F                   G   F  D 
	 *      /
	 *     G  
	 * �����
	 *        A                     A              F              
	 *       / \                   / \            / \
	 *      C   D                 F   D          C   A
	 *     / \        ������->     /      ����            /\    \
	 *    E   F                 C              E  G     D
	 *       /                 / \
	 *      G	              E   G              	       
	 * �����
	 *        A                     A              F              
	 *       / \                   / \            / \
	 *      C   D                 F   D          C   A
	 *     / \        ������->     / \     ����          /   / \
	 *    E   F                 C   G           E   G   D
	 *         \               /   
	 *          G	          E      
	 * ����ģ���ɾ�����ʱ�ſ��ܳ���
	 *        A                     A              F              
	 *       / \                   / \            / \
	 *      C   D                 F   D          C   A
	 *     / \        ������->     / \     ����         / \  / \
	 *    E   F                 C   H          E   G H  D
	 *       / \               / \  
	 *      G   H	          E   G    
	 * ����壬��ɾ�����ʱ�ſ��ܳ���
	 *        A                     C                          
	 *       / \                   / \          
	 *      C   D                 E   A         
	 *     / \        ����->      /   / \     
	 *    E   F                 G   F   D         
	 *   /     \                     \
	 *  G       H	                  H      	            	       	                   	       
	 * */
	private void leftBlance(BiTNode<E> p){
		BiTNode<E> L=p.leftChild;
		switch (L.bf) {
		case LH://���һ,��ߣ���ת֮��߶ȼ�һ��
			p.bf=L.bf=EH;
			rotateRight(p);
			break;
   		case RH:
		    BiTNode<E> Lr=L.rightChild;
		    switch (Lr.bf) {
			case LH://�����
				p.bf=RH;
			    L.bf=EH;
				break;
			case RH://�����
				p.bf=EH;
				L.bf=LH;
				break;
			case EH://����ģ���ɾ�����ʱ�ſ��ܳ���
				p.bf=L.bf=EH;
				break;
			}
		    Lr.bf=EH;
		    rotateLeft(L);
			rotateRight(p);
			break;
		case EH://����壬��ɾ�����ʱ�ſ��ܳ���
		    p.bf=LH;
		    L.bf=RH;
		    rotateRight(p);
			break;
		}
	}
	/*������ƽ�����������С��ƽ�������ĸ������root����ƽ������С��-1
	 * 1��root����������ƽ������Ϊ������ֱ������
	 * 2��root����������ƽ������Ϊ������������������
	 * 3��root����������ƽ������Ϊ0����ֱ������
	 * ���һ       A                       D
	 *       / \                     / \
	 *      C   D                   A   F
	 *         / \        ����->    / \   \
	 *        E   F               C   E   G 
	 *             \
	 *              G
	 * �����
	 *        A                   A              E              
	 *       / \                 / \            / \
	 *      C   D               C   E          A   D
	 *         / \   ������->       / \   ����      / \   \    
	 *        E   F              G   D       C   G   F     
	 *       /                        \
	 *      G	                       F             	       
	 * �����
	 *        A                   A              E             
	 *       / \                 / \            / \
	 *      C   D               C   E          A   D
	 *         / \   ������->          \   ����    /   / \
	 *        E   F                   D      C   G   F   
	 *         \                     /  \
	 *          G	                G    F
	 * ����ģ���ɾ�����ʱ�ſ��ܳ���
	 *        A                  A                 E              
	 *       / \                / \              /  \
	 *      C   D              C   E            A    D
	 *         / \   ������->       / \    ����      / \  / \
	 *        E   F               G   D       C  G H   F
	 *       / \                     / \  
	 *      G   H	                H   F    
	 * ����壬��ɾ�����ʱ�ſ��ܳ���
	 *        A                     D                          
	 *       / \                   / \          
	 *      C   D                 A   F         
	 *         / \        ����->  / \   \     
	 *        E   F             C   E   H         
	 *       /     \               /     
	 *      G       H	          G         	            	       	                   	       
	 * */
	private void rightBlance(BiTNode<E> p){
		BiTNode<E> R=p.rightChild;
		switch (R.bf) {
		case RH://���һ,��ߣ���ת֮��߶ȼ�һ��
			p.bf=R.bf=EH;
			rotateLeft(p);
			break;
   		case LH:
		    BiTNode<E> Rl=R.leftChild;
		    switch (Rl.bf) {
			case LH://�����
				p.bf=EH;
			    R.bf=RH;
				break;
			case RH://�����
				p.bf=LH;
				R.bf=EH;
				break;
			case EH://����ģ���ɾ�����ʱ�ſ��ܳ���
				p.bf=R.bf=EH;
				break;
			}
		    Rl.bf=EH;
		    rotateRight(R);
			rotateLeft(p);
			break;
		case EH://����壬��ɾ�����ʱ�ſ��ܳ���
		    p.bf=RH;
		    R.bf=LH;
		    rotateRight(p);
			break;
		}
	}
	//��������P�������С��ƽ�������ĸ����
	private BiTNode<E> getNotBlance(BiTNode<E> p ){
		BiTNode<E> parent=p.parent;//˫�׽��
		@SuppressWarnings("unchecked")//���Ʋ���Ҫ�ľ���
		Comparable<E> cp=(Comparable<E>) p.value;//ǿתΪ�ɱȽ�
		while (parent!=null) {
			int cmp = cp.compareTo(parent.value);
			if(cmp<0)//��˫�׵�ֵС���������������
				parent.bf++;//ƽ�����Ӽ�һ
			else if(cmp>0)//��˫�׵�ֵ�����������������
				parent.bf--;
			else//�쳣���ڲ����ʱ����Ѿ��ܾ�������ȵ�ֵ�Ľ�㣬���Բ������
				break;
			if(Math.abs(parent.bf)==2)//�ҵ���С��ƽ�������ĸ����
				return parent;
			parent=parent.parent;//�������ϲ���
			
		}
		return null;
	}
	
    /*���������ƽ���Ƿ��ƻ�����������������������
     * 1����С��ƽ�������ĸ������root������2��������������������������ƽ�����
     * 2����С��ƽ�������ĸ�����-2��������������������������ƽ�����
     * */
	private void fixAfterInsert(BiTNode<E> p){
		BiTNode<E> notBlanceNode=getNotBlance(p);//��ȡ��С��ƽ�������ĸ����
		if (notBlanceNode!=null) {
			if(notBlanceNode.bf==2)//���
				leftBlance(notBlanceNode);
			else//�Ҹ�
				rightBlance(notBlanceNode);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean insert(E value){
		BiTNode<E> t=root;//�Ӹ���㿪ʼɨ��
		if (t==null) {//����
			root= new BiTNode<E>(value,null);
			size=1;
			return true;
		}
		BiTNode<E> parent=null;//�������游�ڵ�
		int compResult=0;//0��������ʼ����������ú�߻ᱨ��
		Comparable<E> val=(Comparable<E>) value;
		//�Ӹ���㿪ʼ����������ֱ���ҵ�����λ��
		while (t!=null) {
			parent =t;//���游�ڵ㣻
			compResult=val.compareTo(t.value);
			if(compResult<0)//�ȸ��ڵ��ֵС
				t=t.leftChild;
			else if (compResult>0)//�ȸ��ڵ��ֵ��
				t=t.rightChild;
			else//�Ѿ�����ֵΪvalue�Ľ��
				return false;
		}
		//���ҵ�����λ�ú��½���㲢����
		BiTNode<E> child=new BiTNode<E>(value, parent);
        if(compResult < 0)
        	parent.leftChild=child;
        else
        	parent.rightChild=child;
        fixAfterInsert(child);//����Ϊ�˱���ƽ�⣬Ҫ����Ƿ�ʧ�Ⲣ����Ӧ�ĵ���
        size++;
        
        System.out.println("[ parent:"+child.parent.value+
        		"  parent.leftchild:"+child.parent.leftChild+
        		"  parent.rightchild:"+child.parent.rightChild+"]"+
        		"  [new:"+child+" ]");
        return true;
	}
	
	/**********************
	 * ɾ�������غ���********/
	/*���Һ�̽�㣬Ҳ���ǰ���С�������ڽ��p��ߵĽ��.
	 * ���1(��Q�ĺ�̽��)
	 *      P
	 *     / \    
	 *    A   B
	 *       / \
	 *      C   D
	 *���2����Q�ĺ�̽�㣩
	 *      A
	 *     / \ 
	 *    B   C
	 *     \
	 *      E 
	 *       \
	 *        Q
	 * */
	public BiTNode<E> successor(BiTNode<E> p){
		if (p==null) 
			return null;
		else if (p.rightChild!=null) //�ҵ��Һ��ӣ�֮�������ߵ���ͷ
			return minValue(p.rightChild);
		BiTNode<E> parent=p.parent;
		while(parent!=null && parent.rightChild== p){
			p=parent;
			parent=p.parent;
		}
	    return parent;
	}
	/*ɾ�����
	 * ���1  ��û����������
     *           A                      A
	 *          / \                    / \
	 *         B   C                      C
	 *                ɾ��B ->      
	 * ���2 ��ֻ��������
     *           A                      A
	 *          / \                    / \
	 *         B   C                  D    C
	 *        /        ɾ��B ->      	                         
	 *       D      
	 * ���3  ��ֻ��������
     *           A                      A
	 *          / \                    / \
	 *         B   C                  D    C
	 *          \        ɾ��B ->      	                         
	 *           D 	 
	 * ���4  ������������
     *           A                      A
	 *          / \                    / \
	 *         B   C                  D    C
	 *        / \       ɾ��B ->      	                         
	 *       D 	 E
	 *      / \ / \ 
	 *     F  G H  I  
	 * */
	public void deleteNode(BiTNode<E> p){
		size--;
		/*���p���������������ڣ����ú��s��ֵ���p��ֵ��Ȼ��ɾ�����s.
		 * �������������ͬ��ɾ����̽�㡣ʹ��pָ��s,Ȼ����������������һ�ջ�Ϊ�յĳ���
		 * Ҳ����˵�ǽ�����������Ϊ�յ����ת��Ϊ����������һΪ�ջ�Ϊ�յ����*/
		if(p.leftChild!=null && p.rightChild!= null){//���4
			BiTNode<E> s= successor(p);
			p.value=s.value;//�ú��s��ֵ���p��ֵ,��ͬ��ɾ����p
			p=s;//ɾ��s�Ĺ�����������������һ�ջ�Ϊ�յĳ���
		}
		
		BiTNode<E> replacement =(p.leftChild !=null? p.leftChild:p.rightChild);
		
		if (replacement!=null) {//���2,3
			replacement.parent=p.parent;
			
			if(p.parent==null)
				root=replacement;
			else if(p.parent.leftChild== p)
				p.parent.leftChild=replacement;
			else 
				p.parent.rightChild =replacement;
			
			p.parent=p.leftChild=p.rightChild=null;//��p����������	
			//����ı����replacement�ĸ���㣬ֱ�Ӵ�����ʼ����
			fixAfterDelete(replacement);
			
		}else {//����������Ϊ��,����Ҷ�ӽ��
			fixAfterDelete(p);//ֱ�ӴӸ�Ҷ�ӽ�㿪ʼ����
			if(p.parent == null)//ֻ��һ���������
				root=null;
			else if(p.parent.rightChild==p)
				p.parent.rightChild = null;
			else
				p.parent.leftChild = null;
			p.parent =null;//��������
		}
	}
	/*ɾ�����P��ĵ�������
	 * 1.��P��ʼ���ϻ��ݣ��޸����ȵ�BFֵ������ֻ������P��˫�׵����ڵ��BFֵ
	 * ����ԭ��Ϊ����Pλ��ĳ�����Ƚ��(���A)��������ʱ��A��BF��һ����Pλ��
	 * A��������ʱ��A��BFֵ��һ����ĳ�����ȵ�BFֵ��Ϊ1����-1ʱ��ֹͣ���ݣ�
	 * ����Ͳ���ʽ�෴�ģ�ԭ����ɾ������������ĳ����㲢����ı����ĸ߶�
	 * 
	 * 2.���ÿ���ڵ��BFֵ�����Ϊ2����-2��Ҫ������ת������������������
	 * �����������������ĸ߶Ƚ����ˣ���ô��������������С�����ĸ���㣨��ΪB��
	 * �������ϻ��ݣ�����Ͳ��벻һ������ΪB��˫�׽���ƽ������Ϊ������B�ĸ߶�
	 * �ı���ı䡣����ɾ����Ҫ���ж�ε�����
	 * ��� 1
	 *         A                       A
	 *        / \                     / \
	 *       B   C                   B   C
	 *      / \       ɾ��P-->         \  
	 *     P   D                       D
	 * ��� 2
	 *         A                       A
	 *        / \                     / \
	 *       B   C                   B   C
	 *      / \   \      ɾ��P-->      \   \
  	 *     P   D   F                   D   F
	 *          \                       \
	 *           E                       E
	 * */
	
	private void fixAfterDelete(BiTNode<E> node){
		BiTNode<E> parent = node.parent;
		Comparable e = (Comparable) node.value;
		
		while (parent != null) {
			int cmp = e.compareTo(parent.value);
			if(cmp < 0)
				parent.bf ++;//��������ΪӦ����--
			else
				parent.bf--;
			if(Math.abs(parent.bf)==1)
				break;
			BiTNode<E> r = parent;
			if(parent.bf==2)
				leftBlance(r);
			else
				rightBlance(r);
			parent=parent.parent;
		}
	}
	
	
	/*���������
	 * ��С�����������ֵ*/
/*	public void inOrderTraverse(BiTNode<E> root){
		if(root==null)
			return ;
		inOrderTraverse(root.leftChild);
		System.out.println("[ "+root.value+" ]");
		inOrderTraverse(root.rightChild);
	}*/
	public void inOrderTraverse(BiTNode<E> root){
		if(root==null) 
			return;
		BiTNode<E> t=root;
		Stack<BiTNode<E>> stack=new Stack<BiTNode<E>>();
		while (!stack.isEmpty()|| t!=null) {
			if (t!=null) {
				stack.push(t);
				t=t.leftChild;
			}else {
				t=stack.pop();
				System.out.print(t.value+"  ");
				t=t.rightChild;
			}
		}
		System.out.println();
	}
	
	
	
	private final int LH=1;
	private final int EH=0;
	private final int RH=-1;
	//�������Ķ���������ṹ����,������bf������
	static class BiTNode<E>{
		public E value ;
		public int bf;
		public BiTNode<E> parent;
		public BiTNode<E> leftChild;
		public BiTNode<E> rightChild;
		public BiTNode(E value,BiTNode<E> parent) {
			this.value=value;
			this.parent=parent;
		}
		@Override
		public String toString() {
			return value.toString();
		}
	}
}



