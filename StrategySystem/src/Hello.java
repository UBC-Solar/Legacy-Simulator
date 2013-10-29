public class Hello{
	public static void main(String[] args){
		System.out.println("Hello Phone World");
		Cellphone myPhone;
		Cellphone secondPhone;
		myPhone = new Cellphone("black", "android", 3, 5);
		secondPhone = new Cellphone("white", "apple", 5, myPhone.getCall());
		myPhone.dialNumber(5);
		
		
	}
}