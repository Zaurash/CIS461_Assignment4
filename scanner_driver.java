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
		  LinkedList cur_methods = new LinkedList<String>();
		  CLS cur_class = result.class_list.get(i);
		  String c_name = cur_class.sig.name;
		  String c_super = cur_class.sig.super_class;
		  
		  //Checking if methods are redefined in current class
		  if(cur_class.bd.methods != null){
			  for(int j = 0; j < cur_class.bd.methods.size(); j++){
				  String cm = cur_class.bd.methods.get(j).name;
				  if(cur_methods.contains(cm)){
		      		System.err.println("ERROR: Method " + cm + " defined twice in class " + c_name);
		    			System.exit(1);
				  }
				  
				  cur_methods.add(cm);
		  	
			  }
		  }
		  
		  //Checking if the same class is redefined
		  if(class_names.contains(c_name)){
    		System.err.println("ERROR: Class " + c_name + " defined twice ");
  			System.exit(1);
		  }
		  class_names.add(c_name);
		  super_names.add(c_super);
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