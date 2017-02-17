import java.util.*;

public class Expr{
	
	String constructor_name;
	
	public Expr(){

	}
	
	public Expr(String n, LinkedList<Expr> l){
		this.constructor_name = n;
	}
	
	// Conversion from expr to int
	public static class IntConst extends Expr{
		public int i;
		
		public IntConst(int i){
			this.i = i;
		}
	}
	
	public static IntConst intconst(int i){
		return new IntConst(i);
	}
	
	// Conversion from expr to string
	public static class StringConst extends Expr{
		public String s;
		
		public StringConst(String s){
			this.s = s;
		}
	}
	
	public static StringConst stringconst(String s){
		return new StringConst(s);
	}
	
	public static class Construct extends Expr{
		public Ident i;
		public String name;
		
		public Construct(Ident i){
			this.i = i;
			this.name = i.name;
		}
	}
	
	public static Construct construct(Ident i){
		return new Construct(i);
	}
	
	public static class BinOp extends Expr{
		public int e1, e2;
		public String op;
		public int res;
		
		public BinOp(String op, Expr a, Expr b){

			if(op == "PLUS"){
				this.op = "PLUS";
			}
			
			if(op == "MINUS"){
				this.op = "MINUS";
			}
			
			if(op == "TIMES"){
				this.op = "TIMES";
			}
			
			if(op == "DIVIDE"){
				this.op = "DIVIDE";
			}
			
			this.e1 = e1;
			this.e2 = e2;
		}
	}
	
	public static BinOp binop(String op, Expr a, Expr b){
		return new BinOp(op, a, b);
	}
	
}