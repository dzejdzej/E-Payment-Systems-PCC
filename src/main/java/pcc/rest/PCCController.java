package pcc.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pcc.bean.Issuer;
import pcc.repository.IssuerRepository;

@RestController
@CrossOrigin
@RequestMapping("/pccMain")
public class PCCController {

	@Value("${acquirer.url")
	private String acquirerUrl;

	@Value("${error.origin.name}")
	private String errorOriginName;

	@Autowired
	private IssuerRepository issuerRepository;

	@Autowired
	private RestTemplate rt;

	private final Log logger = LogFactory.getLog(this.getClass());

	@RequestMapping(value = "/completePaymentRequest", method = RequestMethod.POST)
	public ResponseEntity<?> completePayment(@RequestBody CompletePaymentDTO completePaymentDTO) {

		Issuer issuer = issuerRepository.findByPan(completePaymentDTO.getPan());

		String url = "https://" + issuer.getUrl() + "/issuerMain/completePaymentRequest";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<CompletePaymentDTO> completePaymentRequest = new HttpEntity<>(completePaymentDTO);


		CompletePaymentResponseDTO compResponse = null;
		HttpStatus status = HttpStatus.OK;
		try {
			ResponseEntity<CompletePaymentResponseDTO> completePaymentResponse = rt.postForEntity(url,
					completePaymentRequest, CompletePaymentResponseDTO.class);

			compResponse = completePaymentResponse.getBody();
		} catch (HttpClientErrorException e) {
			logger.error(e.getMessage(), e.getCause());
			logger.error("PAYMENT_DTO");
			if (e.getStatusCode() == HttpStatus.CONFLICT) {
				// failed url
				// failed due to...
				logger.error("Payment #" + " failed due to:" + e.getResponseBodyAsString());
				compResponse = CompletePaymentResponseDTO.parse(e.getResponseBodyAsString());
			} 

			status = e.getStatusCode();

		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			logger.error("PAYMENT_DTO");
			compResponse = new CompletePaymentResponseDTO();
			compResponse.setErrorInfo(e.getMessage() + "" + e.getCause());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return ResponseEntity.status(status).body(compResponse);

	}

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<?> exceptionHandlerHttpError(HttpClientErrorException ex) {
		String body = ex.getResponseBodyAsString();
		RestClientExceptionInfo info = new RestClientExceptionInfo();

		if (RestClientExceptionInfo.parse(body) == null) {
			// ova aplikacija je uzrok exceptiona
			// priprema se exception za propagiranje dalje i loguje se
			info.setOrigin(errorOriginName);
			info.setInfo(body);
		} else {
			info.setOrigin(RestClientExceptionInfo.parse(body).getOrigin());
			info.setInfo(RestClientExceptionInfo.parse(body).getInfo());
		}
		logger.error("HttpClientErrorException, info:" + RestClientExceptionInfo.toJSON(info));

		return ResponseEntity.status(ex.getStatusCode()).body(RestClientExceptionInfo.toJSON(info));
	}

}
