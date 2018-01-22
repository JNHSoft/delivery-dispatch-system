package kr.co.cntt.core.api.model;

import java.util.List;

import kr.co.cntt.core.model.Dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * App API 전송데이터 in Generic Request로 any 형태의 Body를 허용
 * 
 * @author su
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
public class GenericRequest<T> implements Dto {
	private static final long serialVersionUID = 7002091651554613826L;

	// array로 규약됨. data구조 개 썅~~~cBody 지랄같네~~
	List<RequestHeader> header;
	List<T> body;
}
