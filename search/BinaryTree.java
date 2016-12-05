import java.util.Stack;

public class BinaryTree {

	private BiTNode root;
	public BinaryTree() {
		this.root=null;
	}
	public BiTNode getRoot(){
		return root;
	}
	
	//最小关键字
	public BiTNode minKey(BiTNode root){
		while (root.leftChild !=null ) 
			root =root.leftChild;
		return root;
	}
	//最大关键字
	public BiTNode maxKey(BiTNode root){
		while(root.rightChild !=null)
			root =root.rightChild;
		return root;
	}
	/*后继
	 * 情况1(找Q的后继结点)
	 *      Q
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
	public BiTNode successor(BiTNode node){
		if(node.rightChild !=null)//情况1
			return minKey(node.rightChild) ;
		BiTNode parent=node.parent;//情况2
		while (parent!=null && parent .rightChild ==node) {
			node=parent;
			parent=node.parent;
		}
		return parent;
	}
	/*前驱
	 * 情况1(找Q的前驱结点)
	 *          Q
	 *         / \    
	 *        B   A
	 *       / \
	 *      C   D
	 *情况2（找Q的前驱结点）
	 *      A
	 *     / \ 
	 *    B   C
	 *       /
	 *      E 
	 *     /
	 *    Q  
	 * */
	//前驱
	public BiTNode predecessor(BiTNode node){
		if(node.leftChild !=null)
			return maxKey(node.leftChild);
		BiTNode parent = node.parent;
		while (parent != null && node== parent.leftChild) {
			node=parent ;
			parent=parent.parent;
		}
		return  parent;
	}
	/*插入
	 * @param root 树根
	 * @param value 要插入的值
	 * @return 插入是否成功*/
	public boolean insert(Integer value){
		if(root ==null){//空树
			root=new BiTNode(value, null);
			return true;
		}	
		if(search(root, value)!=null)
			return false;//如果已经纯在值value
		BiTNode node=root;
		BiTNode parent =null;//用来保存父节点
		while (node != null) {//查找插入位置，也就是parent
			parent =node;
			if(value < node.value)
				node=node.leftChild;
			else
				node = node.rightChild;
		}
		if (value <parent.value)
			parent.leftChild =new BiTNode(value, parent);
		else
			parent.rightChild = new BiTNode(value, parent);
		return true;
	}
	/*删除结点,前提是已经判断树种有该结点
	 * @param value 要插入的值
	 * @return 删除 是否成功
	 * 情况1
	 *         Q                        A
	 *          \         删除Q-->
	 *           A
	 * 情况2
	 *          Q                       A
	 *         /          删除Q-->     
	 *        A
	 * 情况3    
	 *           Q                       A
	 *          / \                     / \  
	 *         B   A       删除Q-->     B   C
	 *              \
	 *               C
	 * 情况4
	 *           Q                       C
	 *          / \                     / \  
	 *         B   A       删除Q-->     B   A
	 *            / \                     / \
	 *           C   D	                 E   D 
	 *            \
	 *             E   
	 * */
	public void delete(BiTNode node){
		if(node.rightChild==null)//情况1
			transplant(node, node.leftChild);
		else if(node.leftChild==null)//情况2
		    transplant(node, node.rightChild);
		else{//左右子树都纯在
			BiTNode minValue = minKey(node.rightChild);
			if(minValue != node.rightChild){//情况4，包括了情况3部分的代码
				transplant(minValue, minValue.rightChild);//把E变到C的位置
				//这些本来应该在transplant(node, minValue)之后执行，在这里可无碍
				minValue.rightChild = node.rightChild;
				minValue.rightChild.parent=minValue;
			}//情况3，A的右子树需要变化
			transplant(node, minValue);
			minValue.leftChild = node.leftChild;
			minValue.leftChild.parent = minValue;
		}
			
			
	}
	/*用一颗v为根的子树替换一颗以u为根的子树
	 * 没有处理u.right和v.left的变化*/
	private void transplant(BiTNode u,BiTNode v){
		if(u.parent== null )
			root = null;//u是根结点
		else if(u == u.parent.leftChild)
			u.parent.leftChild=v;
		else
			u.parent.rightChild=v;
		if(v!=null)
			v.parent=u.parent;
	}
	
	/*查询二叉树
	 * @return 查找到结点返回结点，没有该结点返回null*/
/*	//递归实现
	public BiTNode search(BiTNode root,Integer value){
		if(root == null || value==root.value)
			return root;
		else if(value < root.value)
	
			
			return  search(root.leftChild, value);
		else
			return  search(root.rightChild, value);
	}*/
    //循环实现
	public BiTNode search(BiTNode root,Integer value){
		while(root !=null && value != root.value){
			if(value <root.value)
				root =root.leftChild;
			else
				root=root.rightChild;
		}
		return root;
	}
	
	/*中序遍历*/
/*	//递归方式实现
	public void inOrderWalk(BiTNode<E> root){
		if (root==null) {
			return ;
		}
		inOrderWalk(root.leftChild);
		System.out.println(root.value+",");
		inOrderWalk(root.rightChild);
	}*/
	//循环方式
	public void inOrderWalk(BiTNode root){
		if(root == null)
			return ;
		BiTNode t=root;
		Stack<BiTNode> stack=new Stack<BiTNode>();
		while (t !=null || !stack.isEmpty()) {
			if (t != null) {
			    stack.push(t);
				t=t.leftChild;
			}
			else {
				t=stack.pop();
				System.out.print(t.value+"  ");
				t=t.rightChild;
			}
		}
		System.out.println();
	}
	
	static class BiTNode{
		public Integer value ;
		public int bf;
		public BiTNode parent;
		public BiTNode leftChild;
		public BiTNode rightChild;
		public BiTNode(Integer  value,BiTNode parent) {
			this.value=value;
			this.parent=parent;
		}
	}
}
