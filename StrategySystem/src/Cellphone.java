
public class Cellphone {
	
	String colorOfPhone;
	String typeOfPhone;
	Integer cellNumber;
	Integer dialNumber;
	Integer callNumber;
	public Cellphone(String color, String type, Integer cell, Integer call){
		colorOfPhone = color;
		typeOfPhone = type;
		cellNumber = cell;
		callNumber = call;
		System.out.println("This is a "+ colorOfPhone + " " + typeOfPhone + " phone with number " + cellNumber);
		System.out.println("This phone is calling phone " + callNumber);
		
		
		}
	public void dialNumber(Integer dial){
		dialNumber = dial;
		
		
	}
public int getCall(){
	return callNumber;
}

				
	
	
	
public void enterNumber(int numbertoenter){
	System.out.println("Number entered is " + numbertoenter);
	

}
}



