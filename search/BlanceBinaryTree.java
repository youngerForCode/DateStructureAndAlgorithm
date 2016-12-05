import java.util.Stack;



/***************************
 * 二叉平衡树
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
	/*查找值为value的结点
	 * @param value
	 * @return 值为value的结点*/
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
	
	/*对以p为根的二叉树做右旋处理
	 * 处理之后，p指向新的树根结点，即旋转之前左子树的根结点
	 *           P                      C
	 *          / \                     /\
	 *         C   D                   E  P
	 *        / \        右旋->        /  / \
	 *       E   F                   G   F  D 
	 *      /
	 *     G
	 *           P                      C
	 *          /                      / \
	 *         C                      E   P
	 *        /         右旋->        
	 *       E                              
	 *      
	 *      	     
	 *     
	 * A是最小不平衡子树的根结点    
	 * */
	private void rotateRight(BiTNode<E> p){
		if (p!=null) {
			BiTNode<E> L = p.leftChild;//L指向p的左子树
			p.leftChild=L.rightChild;//L的右子树挂接在p的左子树
			if(L.rightChild!=null)//是不是不用判断
				L.rightChild.parent=p;//结点F的双亲指向P
			L.parent=p.parent;//C的双亲指向P的双亲
            //双亲变化			
			if(p.parent==null)//如果p是头结点
				root=L;
			else if (p.parent.leftChild==p)//p是双亲的左子树 
				p.parent.leftChild=L;
			else//p是双亲的右子树
				p.parent.rightChild=L;
			L.rightChild=p;//右旋，所以p变为L的右子树
			p.parent=L;
		}
		
		
		
	}
	/*对以P为根结点的二叉树做左旋操作
	 * 处理之后，p指向新的根结点，即旋转之前右子树的根结点
	 *           P                       D
	 *          / \                     / \
	 *         C   D                   P   F
	 *            / \        左旋->    / \   \
	 *           E   F               C   E   G 
	 *                \
	 *                 G
	 * A是最小不平衡子树的根结点                                                                                      
	 * */
	private void rotateLeft(BiTNode<E> p){
		BiTNode<E> R;
		R=p.rightChild;//R指向右子树的根结点
		p.rightChild=R.leftChild;//将R的左子树挂在p的右子树上
		//双亲变换
		if(R.leftChild!=null)
			R.leftChild.parent=p;//结点E的双亲指向P
		R.parent=p.parent;//D的双亲指向P的双亲
		if(p.parent==null)//P是根结点
			root=R;
		else if (p.parent.leftChild==p)//p是他的双亲的左子树
			p.parent.leftChild=R;
		else
			p.parent.rightChild=R;//P是他的双亲的右子树
		R.leftChild= p;//左旋，所以R的左子树指向p
		p.parent=R;	
	}
	/*进行左平衡调整，即最小不平衡子树的根（简称root）的平衡因子大于1
	 * 1）root的左子树的平衡因子为正，则直接右旋
	 * 2）root的左子树的平衡因子为负，则先左旋再右旋
	 * 3）root的左子树的平衡因子为0，则直接右旋
	 * 
	 *情况一                A                      C
	 *          / \                     /\
	 *         C   D                   E  A
	 *        / \        右旋->        /  / \
	 *       E   F                   G   F  D 
	 *      /
	 *     G  
	 * 情况二
	 *        A                     A              F              
	 *       / \                   / \            / \
	 *      C   D                 F   D          C   A
	 *     / \        先左旋->     /      右旋            /\    \
	 *    E   F                 C              E  G     D
	 *       /                 / \
	 *      G	              E   G              	       
	 * 情况三
	 *        A                     A              F              
	 *       / \                   / \            / \
	 *      C   D                 F   D          C   A
	 *     / \        先左旋->     / \     右旋          /   / \
	 *    E   F                 C   G           E   G   D
	 *         \               /   
	 *          G	          E      
	 * 情况四，在删除结点时才可能出现
	 *        A                     A              F              
	 *       / \                   / \            / \
	 *      C   D                 F   D          C   A
	 *     / \        先左旋->     / \     右旋         / \  / \
	 *    E   F                 C   H          E   G H  D
	 *       / \               / \  
	 *      G   H	          E   G    
	 * 情况五，在删除结点时才可能出现
	 *        A                     C                          
	 *       / \                   / \          
	 *      C   D                 E   A         
	 *     / \        右旋->      /   / \     
	 *    E   F                 G   F   D         
	 *   /     \                     \
	 *  G       H	                  H      	            	       	                   	       
	 * */
	private void leftBlance(BiTNode<E> p){
		BiTNode<E> L=p.leftChild;
		switch (L.bf) {
		case LH://情况一,左高，旋转之后高度减一。
			p.bf=L.bf=EH;
			rotateRight(p);
			break;
   		case RH:
		    BiTNode<E> Lr=L.rightChild;
		    switch (Lr.bf) {
			case LH://情况二
				p.bf=RH;
			    L.bf=EH;
				break;
			case RH://情况三
				p.bf=EH;
				L.bf=LH;
				break;
			case EH://情况四，在删除结点时才可能出现
				p.bf=L.bf=EH;
				break;
			}
		    Lr.bf=EH;
		    rotateLeft(L);
			rotateRight(p);
			break;
		case EH://情况五，在删除结点时才可能出现
		    p.bf=LH;
		    L.bf=RH;
		    rotateRight(p);
			break;
		}
	}
	/*进行右平衡调整，即最小不平衡子树的根（简称root）的平衡因子小于-1
	 * 1）root的右子树的平衡因子为负，则直接左旋
	 * 2）root的左子树的平衡因子为正，则先右旋再左旋
	 * 3）root的左子树的平衡因子为0，则直接左旋
	 * 情况一       A                       D
	 *       / \                     / \
	 *      C   D                   A   F
	 *         / \        左旋->    / \   \
	 *        E   F               C   E   G 
	 *             \
	 *              G
	 * 情况二
	 *        A                   A              E              
	 *       / \                 / \            / \
	 *      C   D               C   E          A   D
	 *         / \   先右旋->       / \   左旋      / \   \    
	 *        E   F              G   D       C   G   F     
	 *       /                        \
	 *      G	                       F             	       
	 * 情况三
	 *        A                   A              E             
	 *       / \                 / \            / \
	 *      C   D               C   E          A   D
	 *         / \   先右旋->          \   左旋    /   / \
	 *        E   F                   D      C   G   F   
	 *         \                     /  \
	 *          G	                G    F
	 * 情况四，在删除结点时才可能出现
	 *        A                  A                 E              
	 *       / \                / \              /  \
	 *      C   D              C   E            A    D
	 *         / \   先右旋->       / \    左旋      / \  / \
	 *        E   F               G   D       C  G H   F
	 *       / \                     / \  
	 *      G   H	                H   F    
	 * 情况五，在删除结点时才可能出现
	 *        A                     D                          
	 *       / \                   / \          
	 *      C   D                 A   F         
	 *         / \        左旋->  / \   \     
	 *        E   F             C   E   H         
	 *       /     \               /     
	 *      G       H	          G         	            	       	                   	       
	 * */
	private void rightBlance(BiTNode<E> p){
		BiTNode<E> R=p.rightChild;
		switch (R.bf) {
		case RH://情况一,左高，旋转之后高度减一。
			p.bf=R.bf=EH;
			rotateLeft(p);
			break;
   		case LH:
		    BiTNode<E> Rl=R.leftChild;
		    switch (Rl.bf) {
			case LH://情况二
				p.bf=EH;
			    R.bf=RH;
				break;
			case RH://情况三
				p.bf=LH;
				R.bf=EH;
				break;
			case EH://情况四，在删除结点时才可能出现
				p.bf=R.bf=EH;
				break;
			}
		    Rl.bf=EH;
		    rotateRight(R);
			rotateLeft(p);
			break;
		case EH://情况五，在删除结点时才可能出现
		    p.bf=RH;
		    R.bf=LH;
		    rotateRight(p);
			break;
		}
	}
	//查找离结点P最近的最小不平衡子树的根结点
	private BiTNode<E> getNotBlance(BiTNode<E> p ){
		BiTNode<E> parent=p.parent;//双亲结点
		@SuppressWarnings("unchecked")//抑制不必要的警告
		Comparable<E> cp=(Comparable<E>) p.value;//强转为可比较
		while (parent!=null) {
			int cmp = cp.compareTo(parent.value);
			if(cmp<0)//比双亲的值小则插入在了左子树
				parent.bf++;//平衡因子加一
			else if(cmp>0)//比双亲的值大则插入在了右子树
				parent.bf--;
			else//异常，在插入的时候就已经拒绝插入相等的值的结点，所以不会出现
				break;
			if(Math.abs(parent.bf)==2)//找到最小不平衡子树的根结点
				return parent;
			parent=parent.parent;//继续向上查找
			
		}
		return null;
	}
	
    /*插入结点后检查平衡是否破坏，若有则调整。有两种情况
     * 1）最小不平衡子树的根（简称root）等于2，即左子树高于右子树，则左平衡调整
     * 2）最小不平衡子树的根等于-2，即右子树高于左子树，则右平衡调整
     * */
	private void fixAfterInsert(BiTNode<E> p){
		BiTNode<E> notBlanceNode=getNotBlance(p);//获取最小不平衡子树的根结点
		if (notBlanceNode!=null) {
			if(notBlanceNode.bf==2)//左高
				leftBlance(notBlanceNode);
			else//右高
				rightBlance(notBlanceNode);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean insert(E value){
		BiTNode<E> t=root;//从根结点开始扫描
		if (t==null) {//空树
			root= new BiTNode<E>(value,null);
			size=1;
			return true;
		}
		BiTNode<E> parent=null;//用来保存父节点
		int compResult=0;//0是用来初始化，如果不用后边会报错
		Comparable<E> val=(Comparable<E>) value;
		//从根结点开始向下收索，直到找到插入位置
		while (t!=null) {
			parent =t;//保存父节点；
			compResult=val.compareTo(t.value);
			if(compResult<0)//比父节点的值小
				t=t.leftChild;
			else if (compResult>0)//比父节点的值大
				t=t.rightChild;
			else//已经纯在值为value的结点
				return false;
		}
		//查找到插入位置后，新建结点并插入
		BiTNode<E> child=new BiTNode<E>(value, parent);
        if(compResult < 0)
        	parent.leftChild=child;
        else
        	parent.rightChild=child;
        fixAfterInsert(child);//插入为了保持平衡，要检差是否失衡并做相应的调整
        size++;
        
        System.out.println("[ parent:"+child.parent.value+
        		"  parent.leftchild:"+child.parent.leftChild+
        		"  parent.rightchild:"+child.parent.rightChild+"]"+
        		"  [new:"+child+" ]");
        return true;
	}
	
	/**********************
	 * 删除结点相关函数********/
	/*查找后继结点，也就是按大小排序，排在结点p后边的结点.
	 * 情况1(找Q的后继结点)
	 *      P
	 *     / \    
	 *    A   B
	 *       / \
	 *      C   D
	 *情况2（找Q的后继结点）
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
		else if (p.rightChild!=null) //找到右孩子，之后往左走到尽头
			return minValue(p.rightChild);
		BiTNode<E> parent=p.parent;
		while(parent!=null && parent.rightChild== p){
			p=parent;
			parent=p.parent;
		}
	    return parent;
	}
	/*删除结点
	 * 情况1  ：没有左右子树
     *           A                      A
	 *          / \                    / \
	 *         B   C                      C
	 *                删除B ->      
	 * 情况2 ：只有左子树
     *           A                      A
	 *          / \                    / \
	 *         B   C                  D    C
	 *        /        删除B ->      	                         
	 *       D      
	 * 情况3  ：只有右子树
     *           A                      A
	 *          / \                    / \
	 *         B   C                  D    C
	 *          \        删除B ->      	                         
	 *           D 	 
	 * 情况4  ：有左右子树
     *           A                      A
	 *          / \                    / \
	 *         B   C                  D    C
	 *        / \       删除B ->      	                         
	 *       D 	 E
	 *      / \ / \ 
	 *     F  G H  I  
	 * */
	public void deleteNode(BiTNode<E> p){
		size--;
		/*如果p的左右子树都存在，则用后继s的值替代p的值，然后删除后继s.
		 * 所以这种情况等同于删除后继结点。使用p指向s,然后利用左右子树其一空或都为空的程序
		 * 也可以说是将左右子树不为空的情况转换为左右子树其一为空或都为空的情况*/
		if(p.leftChild!=null && p.rightChild!= null){//情况4
			BiTNode<E> s= successor(p);
			p.value=s.value;//用后继s的值替代p的值,等同与删除了p
			p=s;//删除s的工作利用左右子树其一空或都为空的程序
		}
		
		BiTNode<E> replacement =(p.leftChild !=null? p.leftChild:p.rightChild);
		
		if (replacement!=null) {//情况2,3
			replacement.parent=p.parent;
			
			if(p.parent==null)
				root=replacement;
			else if(p.parent.leftChild== p)
				p.parent.leftChild=replacement;
			else 
				p.parent.rightChild =replacement;
			
			p.parent=p.leftChild=p.rightChild=null;//将p的数据清零	
			//这里改变的是replacement的父结点，直接从它开始回溯
			fixAfterDelete(replacement);
			
		}else {//左右子树都为空,即是叶子结点
			fixAfterDelete(p);//直接从该叶子结点开始回溯
			if(p.parent == null)//只有一个结点的情况
				root=null;
			else if(p.parent.rightChild==p)
				p.parent.rightChild = null;
			else
				p.parent.leftChild = null;
			p.parent =null;//数据清零
		}
	}
	/*删除结点P后的调整方法
	 * 1.从P开始向上回溯，修改祖先的BF值，这里只调整从P的双亲到根节点的BF值
	 * 调整原则为，当P位于某个祖先结点(简称A)的左子树时，A的BF减一，当P位于
	 * A的右子树时，A的BF值加一。当某个祖先的BF值变为1或则-1时候停止回溯，
	 * 这里和插入式相反的，原因是删除他的子树的某个结点并不会改变他的高度
	 * 
	 * 2.检查每个节点的BF值，如果为2或则-2需要进行旋转调整，调整方法如下
	 * 如果调整后这个子树的高度降低了，那么必须继续从这个最小子树的根结点（设为B）
	 * 继续向上回溯，这里和插入不一样，因为B的双亲结点的平衡性因为其子树B的高度
	 * 改变二改变。所以删除需要进行多次调整。
	 * 情况 1
	 *         A                       A
	 *        / \                     / \
	 *       B   C                   B   C
	 *      / \       删除P-->         \  
	 *     P   D                       D
	 * 情况 2
	 *         A                       A
	 *        / \                     / \
	 *       B   C                   B   C
	 *      / \   \      删除P-->      \   \
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
				parent.bf ++;//这里我认为应该是--
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
	
	
	/*中序遍历树
	 * 从小到大输出树的值*/
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
	//二叉树的二叉链表结点结构定义,增加了bf数据域
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



