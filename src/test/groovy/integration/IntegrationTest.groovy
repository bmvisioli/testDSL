package integration
import org.apache.http.entity.ContentTypeimport org.junit.Testimport state.TestCaseBuilder
class IntegrationTest extends TestCaseBuilder {		void "Make a JDBC query and validate the result"() {				testCase("Make a JDBC")			.sql("jdbc:oracle:thin:candes/soa@localhost:1521", "SELECT * FROM CANDES.F55CM01P")				.stepName("JDBC CANDES.F55CM01P")				.contains("WR55378")			.sql("jdbc:oracle:thin:candes/soa@localhost:1521", "SELECT * FROM CANDES.F55CM01H")				.contains("1432846281533")					assert context.execute()			}		private String getSoapRequest() {		return """<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">					<soap:Body>					  <CelsiusToFahrenheit xmlns=\"http://www.w3schools.com/webservices/\">						<Celsius>25</Celsius>					  </CelsiusToFahrenheit>					</soap:Body>				  </soap:Envelope>"""	}		private String getRestRequest() {		return """{		    "franchise": {"materialNumber": "WR55378","franchiseCodes": [ "WNI","FEN","VAL"]},		    "received": {"operationDate": "20150210","operationTime": "160112"}			}"""	}		void "Make a Rest request and validate the result"() {				testCase("Make a POST")			.post("http://localhost:8080/rest/material-franchise", getRestRequest(), ContentType.APPLICATION_JSON)				.header("Authorization", "123")					.statusCode(202)			.delay(1000)							assert context.execute()	}		void "Make a SOAP request and validate the result"() {				testCase("Make a POST")			.post("http://www.w3schools.com/webservices/tempconvert.asmx", getSoapRequest(), ContentType.TEXT_XML)				.contains("77")				.statusCode(200)				.xpath("/*[local-name()='Envelope']/*[local-name()='Body']/CelsiusToFahrenheitResponse/CelsiusToFahrenheitResult/text()", "77")			.delay(1000)							assert context.execute()	}		void "Dequeue a message and validate the result"() {		testCase("Dequeue")			.mock("/localb", 8080, "<response/>")				.contains("invalidToken")			.mock("/local", 8080, "<response/>")				.contains("token")						assert context.execute()	}		void "REST Success"() {		def request = """{			    "material": {			        "materialNumber": "WR55378",			        "materialStatus": "A",			        "materialType": "VERP",			        "pmcCode": "12401",			        "picCode": "PCZ1G2OTHERS____1514",			        "netWeight": "5.350",			        "weightUnit": "KG",			        "baseUnit": "EA",			        "purchaseOrderUom": "",			        "labOffice": "JA1",			        "dealerStocking": "",			        "languages": [			            {			                "language": "EN",			                "description": "RIGHT HAND TONGUE"			            },			            {			                "language": "FR",			                "description": "LANGUETTE DROITE"			            },			            {			                "language": "PT",			                "description": "BANDEIRA DIREITO"			            },			            {			                "language": "ES",			                "description": "LENGUETA DERECHA"			            }			        ],			        "plants": [			            {			                "plant": "BR71",			                "unitPack": "1",			                "dfsIndicator": "1"			            },			            {			                "plant": "BR72",			                "unitPack": "1",			                "dfsIndicator": "3"			            }			        ]			    },			    "received": {			        "operationDate": "20150210",			        "operationTime": "160112"			    }			}"""		def sqlDelete = """begin							DELETE FROM HAEDES.F55CM01H;							DELETE FROM HAEDES.F55CM01P;							DELETE FROM HAEDES.F55CM01L;							DELETE FROM CANDES.F55CM01H;							DELETE FROM CANDES.F55CM01P;							DELETE FROM CANDES.F55CM01L;							DELETE FROM MOGDES.F55CM01H;							DELETE FROM MOGDES.F55CM01P;							DELETE FROM MOGDES.F55CM01L;							end"""		def connectionString = "jdbc:oracle:thin:system/oracle@localhost:1521/xe"									testCase("REST Success")			.post("http://localhost:8080/rest/material-master", request, ContentType.APPLICATION_JSON)				.header("Authorization", "123")				.statusCode(202)			.delay(2000)			.sql(connectionString, "SELECT * FROM CANDES.F55CM01P")				.xpath("count(//rows/row[1]/*[text()]) = 6", "true")				.xpath("count(//rows/row) = 2", "true")			.sql(connectionString, "SELECT * FROM CANDES.F55CM01H")				.xpath("count(//rows/row[1]/*[text()]) = 13", "true")				.xpath("count(//rows/row) = 1", "true")			.sql(connectionString, "SELECT * FROM CANDES.F55CM01L")				.xpath("count(//rows/row[1]/*[text()]) = 5", "true")				.xpath("count(//rows/row) = 4", "true")			.sql(connectionString, "SELECT * FROM MOGDES.F55CM01P")				.xpath("count(//rows/row[1]/*[text()]) = 6", "true")				.xpath("count(//rows/row) = 2", "true")			.sql(connectionString, "SELECT * FROM MOGDES.F55CM01H")				.xpath("count(//rows/row[1]/*[text()]) = 13", "true")				.xpath("count(//rows/row) = 1", "true")			.sql(connectionString, "SELECT * FROM MOGDES.F55CM01L")				.xpath("count(//rows/row[1]/*[text()]) = 5", "true")				.xpath("count(//rows/row) = 4", "true")			.sql(connectionString, "SELECT * FROM HAEDES.F55CM01P")				.xpath("count(//rows/row[1]/*[text()]) = 6", "true")				.xpath("count(//rows/row) = 2", "true")			.sql(connectionString, "SELECT * FROM HAEDES.F55CM01H")				.xpath("count(//rows/row[1]/*[text()]) = 13", "true")				.xpath("count(//rows/row) = 1", "true")			.sql(connectionString, "SELECT * FROM HAEDES.F55CM01L")				.xpath("count(//rows/row[1]/*[text()]) = 5", "true")				.xpath("count(//rows/row) = 4", "true")					.sql(connectionString, "DELETE FROM HAEDES.F55CM01H")			.sql(connectionString, "DELETE FROM HAEDES.F55CM01L")			.sql(connectionString, "DELETE FROM HAEDES.F55CM01P")			.sql(connectionString, "DELETE FROM CANDES.F55CM01H")			.sql(connectionString, "DELETE FROM CANDES.F55CM01L")			.sql(connectionString, "DELETE FROM CANDES.F55CM01P")			.sql(connectionString, "DELETE FROM MOGDES.F55CM01H")			.sql(connectionString, "DELETE FROM MOGDES.F55CM01L")			.sql(connectionString, "DELETE FROM MOGDES.F55CM01P")					assert context.execute()	}	
}
