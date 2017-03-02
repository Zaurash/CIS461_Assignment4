import java.io.*;
import java.util.*;
import java.lang.*;


public class scanner_driver {
  static public void main(String argv[]) {    
    /* Start the parser */
	System.out.println("Beginning parse ...");
    try {
      parser p = new parser(new Lexer(new FileReader(argv[0])));
      Program result = (Program)p.parse().value;
	  System.out.println("Parsing completed with no errors");
	  LinkedList class_names = new LinkedList<String>();
	  LinkedList super_names = new LinkedList<String>();
	  LinkedList visited = new LinkedList<String>();
	  
	  for(int i = 0; i < result.class_list.size(); i++){
		  CLS cur_class = result.class_list.get(i);
		  String c_name = cur_class.sig.name;
		  String c_super = cur_class.sig.super_class;
		  			  
		  
		  //Checking if methods are redefined in current class
		  if(cur_class.bd.methods != null){
			  for(int j = 0; j < cur_class.bd.methods.size(); j++){
				  Method cm = cur_class.bd.methods.get(j);

				  if(cur_class.bd.methods.contains(cm.name)){
		      		System.err.println("ERROR: Method " + cm.name + " defined twice in class " + c_name);
		    			System.exit(1);
				  }
				  
		  	
			  }
		  }

		  		  
		  //Checking if the same class is redefined
		  if(class_names.contains(c_name)){
    		System.err.println("ERROR: Class " + c_name + " defined twice ");
  			System.exit(1);
		  }
		  class_names.add(c_name);
		  super_names.add(c_super);
		  
		  
		  
		  //Checking if Method is overridden
		  CLS sup_class = CLS.get_class(result, cur_class.sig.super_class);
		  LinkedList<Method> cur_methods = cur_class.bd.methods;
		  
		  if(sup_class.bd.methods != null){
			  int ssize = sup_class.bd.methods.size();
			  for(int cmeth = 0; cmeth < cur_methods.size(); cmeth++){
				  Method cmt = cur_class.bd.methods.get(cmeth);
				  for(int smeth = 0; smeth < ssize; smeth++){
					  					  
					  if(cur_methods.get(cmeth).name.equals(sup_class.bd.methods.get(smeth).name)){

						  //Check # of args
						 if(cur_methods.get(cmeth).formals.size() != sup_class.bd.methods.get(smeth).formals.size()){
							 System.err.println("ERROR: Overridden method " + cur_methods.get(cmeth).name + " has an incorrect number of arguments");
							 System.exit(1);
						  }
						  
						  //Check argument types
						  for(int bb = 0; bb < cur_methods.get(cmeth).formals.size(); bb++){
							  //call fn that compares 2 arg types
							  if(!CLS.issuper(result, cmt.formals.get(bb).type, sup_class.bd.methods.get(smeth).formals.get(bb).type)){
								  System.err.println("ERROR: Argument " + cmt.formals.get(bb).type + " is not a superclass of " + sup_class.bd.methods.get(smeth).formals.get(bb).type);
								  System.exit(1);
							  }
						  }
					
						  //Check if return type is subtype of superclass' argument
						  if(!CLS.issuper(result, sup_class.bd.methods.get(smeth).ret_type, cmt.ret_type)){
							  System.err.println("ERROR: Return type " + cmt.ret_type + " is not a subclass of " + sup_class.bd.methods.get(smeth).ret_type);
						  }
						  
					  }
				  }
  
			  }
		  }
		  
		  
		  
		  HashMap cl_vars = new HashMap<String, String>();		  
		  
		  //Get hashmap of class arguments
		  for(int j = 0; j < cur_class.sig.formals.size(); j++){
			  cl_vars.put(cur_class.sig.formals.get(j).name, cur_class.sig.formals.get(j).type);
		  }
		  
		  
		  //Check if each method returns the type it is supposed to return
		  for(int j = 0; j < cur_methods.size(); j ++){
			  Method cur_meth = cur_methods.get(j);
			  String ret_tp = cur_meth.ret_type;
			  
			  //Get hashmap of method arguments
			  HashMap<String, String> m_vars = new HashMap<String, String>();
			  for (int ll = 0; ll < cur_meth.formals.size(); ll++){
				  m_vars.put(cur_meth.formals.get(ll).name, cur_meth.formals.get(ll).type);
			  }
			  
			  for(int k = 0; k < cur_meth.stmt_block.size(); k++){
				  Stmt cs = cur_meth.stmt_block.get(k);
				  
				  //check return type for return statement
				  if(Stmt.RetStatement.class.isInstance(cs)){
					  Stmt.RetStatement cst = (Stmt.RetStatement)cs;
					  String temp_type = cst.ret_type;
					  
					  if(temp_type.equals("Ident")){
						  	String r_name = cst.ret_expr.name;
							
						  if(m_vars.get(r_name) == null){
	  						  System.err.println("ERROR: Method " + cur_meth.name + " is supposed to return " + ret_tp + " but instead returns " + r_name + " of unkown type");
							  System.exit(1);
						  }
							
							if(!(m_vars.get(r_name).equals(ret_tp))){
	  						  System.err.println("ERROR: Method " + cur_meth.name + " is supposed to return " + ret_tp + " but instead returns " + m_vars.get(r_name));
	  						  System.exit(1);
							}
					  }
					  else{	
					 
					  	if(!(temp_type.equals(ret_tp))){
							System.err.println("ERROR: Method " + cur_meth.name + " is supposed to return " + ret_tp + " but instead returns " + temp_type);
						  	System.exit(1);
					  }
				  	}
				  }
				  
				  //check return type for if statement
				  if(Stmt.IfStmt.class.isInstance(cur_meth.stmt_block.get(k))){
					  Stmt.IfStmt cst = (Stmt.IfStmt)cs;
					  for(int l = 0; l < cst.ifstmts.size(); l++){
						  for(int t = 0; t < cst.ifstmts.get(l).stmts.size(); t++){
							  if(Stmt.RetStatement.class.isInstance(cst.ifstmts.get(l).stmts.get(t))){
								  Stmt.RetStatement cr = (Stmt.RetStatement)cst.ifstmts.get(l).stmts.get(t);

								  String temp_type = cr.ret_type;
					  
								  if(temp_type.equals("Ident")){
									  	String r_name = cr.ret_expr.name;
							
									  if(m_vars.get(r_name) == null){
				  						  System.err.println("ERROR: Method " + cur_meth.name + " is supposed to return " + ret_tp + " but instead returns " + r_name + " of unkown type in one of its if/elif/else blocks");
										  System.exit(1);
									  }
							
										if(!(m_vars.get(r_name).equals(ret_tp))){
				  						  System.err.println("ERROR: Method " + cur_meth.name + " is supposed to return " + ret_tp + " but instead returns " + m_vars.get(r_name) + " in one of its if/elif/else blocks");
				  						  System.exit(1);
										}
								  }
								  else{	
					 
								  	if(!(temp_type.equals(ret_tp))){
										System.err.println("ERROR: Method " + cur_meth.name + " is supposed to return " + ret_tp + " but instead returns " + temp_type + " in one of its if/elif/else blocks");
									  	System.exit(1);
								  }
							  	}
						  	}

						  }
					  }
				  }
				  
				  //check return type for while statement
				  if(Stmt.WhileStmt.class.isInstance(cur_meth.stmt_block.get(k))){
					  Stmt.WhileStmt cst = (Stmt.WhileStmt)cs;
					  for(int l = 0; l < cst.stmts.size(); l++){

						  if(Stmt.RetStatement.class.isInstance(cst.stmts.get(l))){
							  Stmt.RetStatement cr = (Stmt.RetStatement)cst.stmts.get(l);

							  String temp_type = cr.ret_type;
				  
							  if(temp_type.equals("Ident")){
								  	String r_name = cr.ret_expr.name;
						
								  if(m_vars.get(r_name) == null){
			  						  System.err.println("ERROR: Method " + cur_meth.name + " is supposed to return " + ret_tp + " but instead returns " + r_name + " of unkown type in while block");
									  System.exit(1);
								  }
						
									if(!(m_vars.get(r_name).equals(ret_tp))){
			  						  System.err.println("ERROR: Method " + cur_meth.name + " is supposed to return " + ret_tp + " but instead returns " + m_vars.get(r_name) + " in while block");
			  						  System.exit(1);
									}
							  }
							  else{	
				 
							  	if(!(temp_type.equals(ret_tp))){
									System.err.println("ERROR: Method " + cur_meth.name + " is supposed to return " + ret_tp + " but instead returns " + temp_type + " in while block");
								  	System.exit(1);
							  }
						  	}
					  	}
						
					  }
				  	
				  }
				  
				  
				  
				  //finish loop of method statements
			  }
		  }
		  
		  
		  //Check fields and get names and types
		  HashMap cur_fields = new HashMap<String, String>();
		  LinkedList<Stmt> cl_stmts = cur_class.bd.statements;
		  if(cl_stmts != null){  
			  for(int j = 0; j < cl_stmts.size(); j++){
				  if(Stmt.AssignStmt.class.isInstance(cl_stmts.get(j))){
					  Stmt.AssignStmt c_stmt = (Stmt.AssignStmt)cl_stmts.get(j);
					  if(cur_fields.get(c_stmt.l_expr.name) == null){
						  if(c_stmt.r_expr.type == "Ident"){
							  if(cl_vars.get(c_stmt.r_expr.name) == null){
								  System.err.println("ERROR field " + c_stmt.l_expr.name + " in class " + c_name + " initialized to unknown type");
								  System.exit(1);
							  }
							  else{
								  cur_fields.put(c_stmt.l_expr.name, cl_vars.get(c_stmt.l_expr.name));					  
							  }
						  }
						  
						  else{
							  cur_fields.put(c_stmt.l_expr.name, c_stmt.r_expr.type);
						  }
					  }
				  }
			  }
			  
			  //Checking if subclasses initialize fields of superclass
			  for(int j = 0; j < result.class_list.size(); j++){
				  if(result.class_list.get(j).sig.super_class.equals(cur_class.sig.name)){
					  
					  CLS q_class = result.class_list.get(j);
					  
					  HashMap<String, String> temp_fields = cur_fields;
					  HashMap<String, String> temp_vars = new HashMap<String, String>();
					  
					  for(int l = 0; l < q_class.sig.formals.size(); l++){
						  
						  temp_vars.put(q_class.sig.formals.get(l).name, q_class.sig.formals.get(l).type);
					  }
					  
					  for(int l = 0; l < q_class.bd.statements.size(); l++){
						  
						  if(Stmt.AssignStmt.class.isInstance(q_class.bd.statements.get(l))){
							  Stmt.AssignStmt t_stmt = (Stmt.AssignStmt)q_class.bd.statements.get(l);
							  if(temp_fields.containsKey(t_stmt.l_expr.name)){
								  if(temp_fields.get(t_stmt.l_expr.name) == null){									  
								  	if(t_stmt.r_expr.type == "Ident"){
									  System.err.println("ERROR field " + t_stmt.l_expr.name + " in class " + q_class.sig.name + " initialized to unknown type");
									  System.exit(1);
								  	}
								  else{
									  
									  temp_fields.put(t_stmt.l_expr.name, temp_vars.get(t_stmt.l_expr.name));
								  }
							  	}

							  }
							  if(temp_fields.get(t_stmt.l_expr.name)!= null){
								  if(CLS.issuper(result, temp_fields.get(t_stmt.l_expr.name), t_stmt.r_expr.type)){
									  temp_fields.remove(t_stmt.l_expr.name);
								  }
							  }

						  }
					  }
					  if(!(temp_fields.isEmpty())){
						  System.err.println("ERROR not all fields from class " + c_name + " initialized properly in subclass " + q_class.sig.name);
						  System.exit(1);
					  }
				  }					
			  }		  		  	
		  }
		  
		  //Checking class arguments NYI (Not sure if we need to)
		 /* for (int fi = 0; fi < cur_class.sig.formals.size(); fi++){
			  CLS temp = CLS.get_class(result, cur_class.sig.formals.get(fi).type);
			  if(temp.sig != null){
				  temp = CLS.get_class(result, temp.sig.super_class);
			  }
			  
			  System.out.println(cur_class.sig.formals.get(fi).type);
		  }	 */ 
		  
		  
	  }
	  
	  
	  // Tests to see if superclasses are existing classes 
	  for(int i = 0; i < result.class_list.size(); i++){
		  String temp = result.class_list.get(i).sig.super_class;
		  if(!class_names.contains(temp)){
  		  	System.err.println("ERROR: super class " + temp + " does not exist ");
			System.exit(1);
		  }
		  // Check if there are any loops in the class hierarchy
		  else{
			  CLS.loop_check(result, visited, temp);
		  }
	  } 
	  
	  
	  // Tests to see if constructor calls have existing classes
	  for(int i = 0; i < result.statement_list.size(); i++){
		  Stmt temp = result.statement_list.get(i);
		  if (Stmt.CStatement.class.isInstance(result.statement_list.get(i))){
			  Stmt.CStatement nt = (Stmt.CStatement)result.statement_list.get(i);
				  if (CLS.check_super(result, nt.constructor_name)){
				  }
				  else {
					  System.err.println("ERROR: called constructor " + nt.constructor_name + " which calls a class that does not exist");
					  System.exit(1);
				  }
			 
		  }

	  }

	  
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}