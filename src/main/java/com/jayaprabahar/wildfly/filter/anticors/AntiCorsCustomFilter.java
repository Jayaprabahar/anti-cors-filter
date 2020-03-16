/**
 * 
 */
package com.jayaprabahar.wildfly.filter.anticors;

import org.apache.commons.lang3.StringUtils;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.StatusCodes;

/**
 * <p> Project : com.jayaprabahar.wildfly.filter.anticors </p>
 * <p> Title : AntiCorsCustomFilter.java </p>
 * <p> Description: Wildfly custom filter to prevent CORS calls and stop CSRF attacks</p>
 * <p> Created: Feb 3, 2020 </p>
 * 
 * @version 6.0.0
 * @author <a href="mailto:jpofficial@gmail.com">Jayaprabahar</a>
 */
public class AntiCorsCustomFilter implements HttpHandler {

	public static final String HTTP_HEADER_REFERER = "Referer";
	public static final String HTTP_HEADER_ORIGIN = "Origin";
	public static final String HTTP_HEADER_SECFETCHSITE = "Sec-Fetch-Site";
	public static final String HTTP_HEADER_SECFETCHSITE_CROSSSITE = "cross-site";
	public static final String POSSIBLE_REFERER_NAMES = "PossibleRefererNames";
	private String possibleRefererString = System.getenv(POSSIBLE_REFERER_NAMES);

	private HttpHandler next;

	public AntiCorsCustomFilter(HttpHandler next) {
		this.next = next;
	}

	/**
	 * Handler for the request
	 * @throws Exception 
	 */

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		HeaderMap requestHeadermap = exchange.getRequestHeaders();
		if (hasValidRefererHeader(requestHeadermap.getFirst(HTTP_HEADER_REFERER)) || hasValidRefererHeader(requestHeadermap.getFirst(HTTP_HEADER_ORIGIN))
				|| isNotCrossSiteSecFetchSiteHeader(requestHeadermap.getFirst(HTTP_HEADER_SECFETCHSITE))) {
			next.handleRequest(exchange);
		} else {
			exchange.setStatusCode(StatusCodes.FORBIDDEN);
			exchange.endExchange();
			System.out.println("CSRF attack found with request " + exchange.getRequestURL() + "for " + exchange.getRequestMethod() + " with param " + exchange.getPathParameters()
					+ " and headers " + requestHeadermap);
		}
		return;
	}

	/**
	 * Validates whether the given refererHeaderis valid or not
	 * 
	 * @param refererHeader
	 * @return Boolean - Based on the referrer details, returns TRUE if Referer
	 *         details are wrong. Else returns FALSE.
	 */
	private boolean hasValidRefererHeader(String refererHeader) {
		return StringUtils.startsWithAny(refererHeader, StringUtils.split(possibleRefererString, ","));
	}

	/**
	 * Validates whether the given secFetchSiteHeader is not cross-site or not
	 * 
	 * @param secFetchSiteHeader
	 * @return Boolean - TRUE is the sec-fetch-site value is not cross-site. If not present, FALSE
	 */
	private boolean isNotCrossSiteSecFetchSiteHeader(String secFetchSiteHeader) {
		return !StringUtils.equalsIgnoreCase(secFetchSiteHeader, HTTP_HEADER_SECFETCHSITE_CROSSSITE);
	}

}
