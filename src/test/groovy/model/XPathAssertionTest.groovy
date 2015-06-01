package model

import org.junit.Test

class XPathAssertionTest {
	
	@Test
	void "A valid xpath return true"() {
		def tested = new XPathAssertion(xpath:"/root/child/text()", text:"value")
		def xml = "<root><child>value</child></root>"
		
		assert tested.assertCondition(new HttpResponse(response:xml))
	}
	
	@Test
	void "A invalid xpath return false"() {
		def tested = new XPathAssertion(xpath:"/root/child/text()", text:"invalid")
		def xml = "<root><child>value</child></root>"
		
		assert !tested.assertCondition(new HttpResponse(response:xml))
	}

}
