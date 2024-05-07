import java.util.*;
import java.io.*;
class Employee implements Comparable{
	int id;
	String name;
	float sal;
	int exp;    //experience
	Employee(int id,String name,float sal,int exp){
		this.id=id;
		this.name=name;
		this.sal=sal;
		this.exp=exp;
	}
	public String toString(){
		return id+","+name+","+sal+","+exp;
	}
	public int compareTo(Object obj){
		Employee e = (Employee)obj;
		return (this.exp-e.exp);
	}
}
public class EmployeeTest{
	public static void main(String[] args) throws Exception{
		File f = new File("C:\\Users\\LENOVO\\OneDrive\\Documents\\EmployeeData.csv");
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String data;
		String info[];
		Employee[] earr= new Employee[4];
		for(int i=0;i<4;i++){
			data = br.readLine();
			info=data.split(",");
			earr[i] = new Employee(Integer.parseInt(info[0]),info[1],Float.parseFloat(info[2]),Integer.parseInt(info[3]));
		}
		// System.out.println("THIS IS PASSED!");
		Arrays.sort(earr);
		for(int i=0;i<4;i++){
			System.out.println(earr[i]);
		}
		String path1 = "C:\\Users\\LENOVO\\OneDrive\\Documents\\sorted.csv";
		FileWriter fw = new FileWriter(path1);
		for(int i=0;i<4;i++){
			fw.write(earr[i]+"\n");
		}
		fw.close();
	}
}