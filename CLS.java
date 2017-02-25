import java.util.*;

public class CLS{
		
	public CLS.Signature sig;
	public CLS.Body bd;
	
	public CLS(){
		
	}
		
	/*public CLS(String n, String s){
		this.name = n;
		this.super_class = s;
	}*/
	
	public CLS(Signature s, Body b){
		this.sig = s;
		this.bd = b;
	}
		
		public static Boolean check_super(Program res, String s){
	  	  for(int inc = 0; inc < res.class_list.size(); inc++){
	  		  String temp = res.class_list.get(inc).sig.name;
	  				  if(s.equals(temp)){
					return true;	  				  	
	  		  }
	  	  }
		  return false;
		}
		
		public static int loop_check(Program res, LinkedList visit, String s){			
		  if(s == "Obj"){
			  return 0;
		  }
		  
  	  	  for(int inc = 0; inc < res.class_list.size(); inc++){
			  Signature temp = res.class_list.get(inc).sig;
			  
			  if(s.equals(temp.name)){

				  if(visit.contains(s)){
					  System.err.println("ERROR: Loop Detected in Class Hierarchy involving " + s);
					  System.exit(1);
				  }
				visit.add(s);

				loop_check(res, visit, temp.super_class);
			  }
		  }
		return 0;
		}
		
		public static class Signature extends CLS{
			public String name;
			public String super_class;
			
			public Signature(String n, String s){
				this.name = n;
				this.super_class = s;
			}
		}
		
		public static Signature signature(String n, String s){
			return new Signature(n,s);
		}
		
		public static class Body extends CLS{
			public LinkedList<Method> methods;
			public LinkedList<Stmt> statements;
			
			public Body(LinkedList<Stmt> s, LinkedList<Method> m){
				this.statements = s;
				this.methods = m;
			}
			
			public Body(){
				this.statements = null;
				this.methods = null;
			}
			
			
		}
		
		public static Body body(LinkedList<Stmt> s, LinkedList<Method> m){
			return new Body(s, m);
		}


}