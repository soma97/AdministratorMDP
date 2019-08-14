
/**
 * MDPSoapServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */

    package axis;

    /**
     *  MDPSoapServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class MDPSoapServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public MDPSoapServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public MDPSoapServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for addEmployee method
            * override this method for handling normal response from addEmployee operation
            */
           public void receiveResultaddEmployee(
                    axis.MDPSoapServiceStub.AddEmployeeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addEmployee operation
           */
            public void receiveErroraddEmployee(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for blockEmployee method
            * override this method for handling normal response from blockEmployee operation
            */
           public void receiveResultblockEmployee(
                    axis.MDPSoapServiceStub.BlockEmployeeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from blockEmployee operation
           */
            public void receiveErrorblockEmployee(java.lang.Exception e) {
            }
                


    }
    