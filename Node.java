import java.util.*;

public class Node{
	
	public LinkedList<CLS> class_list;
	public LinkedList<Stmt> statement_list;
	
	public Node(LinkedList<CLS> c, LinkedList<Stmt> s){
		this.class_list = c;
		this.statement_list = s;
	}
	
}