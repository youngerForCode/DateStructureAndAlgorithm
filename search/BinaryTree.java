import java.util.Stack;

public class BinaryTree {

	private BiTNode root;
	public BinaryTree() {
		this.root=null;
	}
	public BiTNode getRoot(){
		return root;
	}
	
	//��С�ؼ���
	public BiTNode minKey(BiTNode root){
		while (root.leftChild !=null ) 
			root =root.leftChild;
		return root;
	}
	//���ؼ���
	public BiTNode maxKey(BiTNode root){
		while(root.rightChild !=null)
			root =root.rightChild;
		return root;
	}
	/*���
	 * ���1(��Q�ĺ�̽��)
	 *      Q
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
	public BiTNode successor(BiTNode node){
		if(node.rightChild !=null)//���1
			return minKey(node.rightChild) ;
		BiTNode parent=node.parent;//���2
		while (parent!=null && parent .rightChild ==node) {
			node=parent;
			parent=node.parent;
		}
		return parent;
	}
	/*ǰ��
	 * ���1(��Q��ǰ�����)
	 *          Q
	 *         / \    
	 *        B   A
	 *       / \
	 *      C   D
	 *���2����Q��ǰ����㣩
	 *      A
	 *     / \ 
	 *    B   C
	 *       /
	 *      E 
	 *     /
	 *    Q  
	 * */
	//ǰ��
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
	/*����
	 * @param root ����
	 * @param value Ҫ�����ֵ
	 * @return �����Ƿ�ɹ�*/
	public boolean insert(Integer value){
		if(root ==null){//����
			root=new BiTNode(value, null);
			return true;
		}	
		if(search(root, value)!=null)
			return false;//����Ѿ�����ֵvalue
		BiTNode node=root;
		BiTNode parent =null;//�������游�ڵ�
		while (node != null) {//���Ҳ���λ�ã�Ҳ����parent
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
	/*ɾ�����,ǰ�����Ѿ��ж������иý��
	 * @param value Ҫ�����ֵ
	 * @return ɾ�� �Ƿ�ɹ�
	 * ���1
	 *         Q                        A
	 *          \         ɾ��Q-->
	 *           A
	 * ���2
	 *          Q                       A
	 *         /          ɾ��Q-->     
	 *        A
	 * ���3    
	 *           Q                       A
	 *          / \                     / \  
	 *         B   A       ɾ��Q-->     B   C
	 *              \
	 *               C
	 * ���4
	 *           Q                       C
	 *          / \                     / \  
	 *         B   A       ɾ��Q-->     B   A
	 *            / \                     / \
	 *           C   D	                 E   D 
	 *            \
	 *             E   
	 * */
	public void delete(BiTNode node){
		if(node.rightChild==null)//���1
			transplant(node, node.leftChild);
		else if(node.leftChild==null)//���2
		    transplant(node, node.rightChild);
		else{//��������������
			BiTNode minValue = minKey(node.rightChild);
			if(minValue != node.rightChild){//���4�����������3���ֵĴ���
				transplant(minValue, minValue.rightChild);//��E�䵽C��λ��
				//��Щ����Ӧ����transplant(node, minValue)֮��ִ�У���������ް�
				minValue.rightChild = node.rightChild;
				minValue.rightChild.parent=minValue;
			}//���3��A����������Ҫ�仯
			transplant(node, minValue);
			minValue.leftChild = node.leftChild;
			minValue.leftChild.parent = minValue;
		}
			
			
	}
	/*��һ��vΪ���������滻һ����uΪ��������
	 * û�д���u.right��v.left�ı仯*/
	private void transplant(BiTNode u,BiTNode v){
		if(u.parent== null )
			root = null;//u�Ǹ����
		else if(u == u.parent.leftChild)
			u.parent.leftChild=v;
		else
			u.parent.rightChild=v;
		if(v!=null)
			v.parent=u.parent;
	}
	
	/*��ѯ������
	 * @return ���ҵ���㷵�ؽ�㣬û�иý�㷵��null*/
/*	//�ݹ�ʵ��
	public BiTNode search(BiTNode root,Integer value){
		if(root == null || value==root.value)
			return root;
		else if(value < root.value)
	
			
			return  search(root.leftChild, value);
		else
			return  search(root.rightChild, value);
	}*/
    //ѭ��ʵ��
	public BiTNode search(BiTNode root,Integer value){
		while(root !=null && value != root.value){
			if(value <root.value)
				root =root.leftChild;
			else
				root=root.rightChild;
		}
		return root;
	}
	
	/*�������*/
/*	//�ݹ鷽ʽʵ��
	public void inOrderWalk(BiTNode<E> root){
		if (root==null) {
			return ;
		}
		inOrderWalk(root.leftChild);
		System.out.println(root.value+",");
		inOrderWalk(root.rightChild);
	}*/
	//ѭ����ʽ
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
