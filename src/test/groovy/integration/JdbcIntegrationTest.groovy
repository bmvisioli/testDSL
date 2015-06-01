package integration
import org.apache.http.entity.ContentType;import org.junit.Testimport state.TestCaseBuilder
class JdbcIntegrationTest extends TestCaseBuilder {		void "Make a JDBC query and validate the result"() {				testCase("Make a JDBC")			.sql("jdbc:oracle:thin:candes/soa@localhost:1521", "SELECT * FROM CANDES.F55CM01P")				.stepName("JDBC CANDES.F55CM01P")				.contains("WR55378")			.sql("jdbc:oracle:thin:candes/soa@localhost:1521", "SELECT * FROM CANDES.F55CM01H")				.contains("1432846281533")					assert context.execute()			}		private String getRequest() {		return """<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">					<soap:Body>					  <CelsiusToFahrenheit xmlns=\"http://www.w3schools.com/webservices/\">						<Celsius>25</Celsius>					  </CelsiusToFahrenheit>					</soap:Body>				  </soap:Envelope>"""	}		@Test	void "Make a Http request and validate the result"() {				testCase("Make a POST")			.post("http://www.w3schools.com/webservices/tempconvert.asmx", getRequest(), ContentType.TEXT_XML)				.contains("77")				.statusCode(200)				.xpath("/*[local-name()='Envelope']/*[local-name()='Body']/CelsiusToFahrenheitResponse/CelsiusToFahrenheitResult/text()", "77")			.delay(1000)							assert context.execute()			}		
}
