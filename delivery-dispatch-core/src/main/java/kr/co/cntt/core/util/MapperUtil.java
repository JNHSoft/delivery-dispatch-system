package kr.co.cntt.core.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import kr.co.cntt.core.model.kakao.KakaoAddr;
import kr.co.cntt.core.model.kakao.KakaoMenu;
import kr.co.cntt.core.model.kakao.KakaoOrder;
import kr.co.cntt.core.model.kakao.KakaoPayInfo;

/**
 * 클래스 매퍼
 *
 * @author brad
 */
public class MapperUtil {
	/**
	 * KakaoOrder -> Order
	 *
	 * @param kakaoOrder
	 * @return
	 */
	public static Order from(final KakaoOrder kakaoOrder) {
		final Order order = new Order();
		order.setKakaoOrder(true);

		//조회순번
		order.setNo(kakaoOrder.getRownum());

		//주문
		order.setAState(kakaoOrder.getOrder_state());
		order.setAOrderId(kakaoOrder.getOrder_id());
		order.setAOrderMemo(kakaoOrder.getComment());
		order.setAOrderFlag("kakao");
		order.setAOrderType(kakaoOrder.getOrder_cd());
		order.setPhone(kakaoOrder.getPhone_number());

		//매장
		order.setABranchId(kakaoOrder.getBranch_code());
		order.setABranchName(kakaoOrder.getBranch_name());

		//주문일시
		final LocalDateTime orderDatetime = DateUtil.strToLocalDatetime(kakaoOrder.getOrder_time(),
				"yyyy-MM-dd HH:mm:ss");
		order.setAOrderDate(DateUtil.localDatetimeToStr(orderDatetime, "yyyyMMdd"));
		order.setAOrderTime(DateUtil.localDatetimeToStr(orderDatetime, "HHmmss"));

		//예약일시
		final LocalDateTime bookDatetime = DateUtil.strToLocalDatetime(kakaoOrder.getBook_time(),
				"yyyy-MM-dd HH:mm:ss");
		order.setAReservDate(bookDatetime != null ? DateUtil.localDatetimeToStr(bookDatetime, "yyyyMMdd") : null);
		order.setAReservTime(bookDatetime != null ? DateUtil.localDatetimeToStr(bookDatetime, "HHmmss") : null);
		order.setTime((int) (bookDatetime != null ? ChronoUnit.MINUTES.between(LocalDateTime.now(), bookDatetime)
				: ChronoUnit.MINUTES.between(LocalDateTime.now(), orderDatetime)));
		order.setABox(bookDatetime != null ? "Y" : "N");

		//주소
		final KakaoAddr kakaoAddr = kakaoOrder.getAddress();
		order.setImsi(order.getABranchName() + kakaoAddr.getArea());
		order.setSi(kakaoAddr.getSi());
		order.setGu(kakaoAddr.getGu());
		order.setDong(kakaoAddr.getDong());
		order.setAddr1(StringUtil.concatStrings(" ", null, kakaoAddr.getRi(), kakaoAddr.getBunji()));
		order.setAddr2(kakaoAddr.getBuilding_name());
		order.setAddrDesc(kakaoAddr.getDetail());

		//주문금액
		order.setArvAmt(BigDecimal.ZERO);
		for (final KakaoPayInfo payInfo : kakaoOrder.getPay_info()) {
			order.setArvAmt(order.getArvAmt().add(payInfo.getPrice()));
			order.setPayCd(payInfo.getPay_cd());
		}

		//메뉴정보
		final ArrayList<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (final KakaoMenu kakaoMenu : kakaoOrder.getMenus()) {
			orderItems.add(new OrderItem(kakaoMenu.getName(), kakaoMenu.getAmount(), kakaoMenu.getPrice()));
		}
		order.setOrderItems(orderItems);

		return order;
	}
}
