import java.util.*;

public class Stmt{
	
	public String constructor_name;
	
	public Stmt(){
		
	}
	
	public Stmt(Expr n, LinkedList<Stmt> stmts){
		this.constructor_name = n.constructor_name;
	}
	
	public class WhileStmt extends Stmt{
		public Expr condition;
		public LinkedList<Stmt> stmts;
		
		public WhileStmt(Expr e, LinkedList<Stmt> stmts){
			this.condition = e;
			this.stmts = stmts;
		}
	}
	
	public class IfStmt extends Stmt{
		public LinkedList<IFBRANCH> ifbranch;
		
		public IfStmt(){
			
		}
	
	}
	
	public class AssignStmt extends Stmt{
		public Expr L_Expr;
		public Expr R_Expr;
		
		public AssignStmt(){
			
		}
		
		public AssignStmt(Expr l_expr, Expr r_expr){
			this.L_Expr = l_expr;
			this.R_Expr = r_expr;
		}
	} 
	
	public static class IFBRANCH extends Stmt{
		Expr condition;
		LinkedList<Stmt> stmts;
		
		public IFBRANCH(Expr condition, LinkedList<Stmt> stmts){
			this.condition = condition;
			this.stmts = stmts;
		}
	}
	
	public static IFBRANCH ifbranch(Expr condition, LinkedList<Stmt> stmts){
		return new IFBRANCH(condition, stmts);
	}	
	
}