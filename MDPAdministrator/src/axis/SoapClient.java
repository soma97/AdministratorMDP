package axis;

import main.Main;

public class SoapClient {
	public static boolean addEmployee(String username,String passwordHash)
	{
		try {
			MDPSoapServiceStub stub = new MDPSoapServiceStub();
		 	MDPSoapServiceStub.AddEmployee request = new MDPSoapServiceStub.AddEmployee();

	     	request.setUsername(username);
	     	request.setPasswordHash(passwordHash);

	     	MDPSoapServiceStub.AddEmployeeResponse response = stub.addEmployee(request);
	     	return response.get_return();
		}catch(Exception e)
		{
			e.printStackTrace();
			Main.setErrorLog(e);
		}
		return false;
	}
	public static boolean blockEmployee(String username,String passwordHash)
	{
		try {
			MDPSoapServiceStub stub = new MDPSoapServiceStub();
			MDPSoapServiceStub.BlockEmployee request = new MDPSoapServiceStub.BlockEmployee();

			request.setUsername(username);
			request.setPassswordHash(passwordHash);

			MDPSoapServiceStub.BlockEmployeeResponse response = stub.blockEmployee(request);
			return response.get_return();
		}catch(Exception e)
		{
			e.printStackTrace();
			Main.setErrorLog(e);
		}
		return false;
	}
}
