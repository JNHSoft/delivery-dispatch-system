package kr.co.cntt.core.api.model;

import kr.co.cntt.rest.interfaces.Dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * App API 전송데이터 out
 * Generic Response로 any 형태의 Body를 허용
 * @author su
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
public class GenericResponse<T> implements Dto{
	private static final long serialVersionUID = 7002091651554613826L;
	
	ResponseHeader header;
	T body;
	public GenericResponse(ResponseHeader header, T body) {
		this.header = header;
		this.body = body;
	}
}