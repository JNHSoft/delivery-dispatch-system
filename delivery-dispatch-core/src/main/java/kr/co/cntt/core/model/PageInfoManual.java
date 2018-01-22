package kr.co.cntt.core.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.pagehelper.PageInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 쿼리 없이 수동 페이징 가능하도록 {@link PageInfo}를 확장한 클래스
 *
 * @author brad
 * @param <T>
 * @see PageInfo
 */
@Getter
@Setter
public class PageInfoManual<T> extends PageInfo<T> {

	private static final long serialVersionUID = 8387849229769744983L;

	/**
	 * 쿼리 없이 수동으로 페이징 설정
	 *
	 * @param list 아이템 목록
	 * @param pageNum 조회된 페이지
	 * @param pageSize 한 페이지에 보여지는 아이템 수
	 * @param navigatePages 페이징 바에 보여줄 페이지 수
	 * @param total_count 전체 아이템 수
	 */
	public PageInfoManual(final List<T> list, final int navigatePages, final Integer pageNum, final Integer pageSize,
			final Integer total_count) {
		super(list, navigatePages);

		initPageInfo(pageNum, pageSize, navigatePages, total_count);
	}

	/**
	 * 수동으로 페이징 설정
	 *
	 * @param pageNum 조회된 페이지
	 * @param pageSize 한 페이지에 보여지는 아이템 수
	 * @param navigatePages 페이징 바에 보여줄 페이지 수
	 * @param total_count 전체 아이템 수
	 */
	public void initPageInfo(final Integer pageNum, final Integer pageSize, final Integer navigatePages,
			final Integer total_count) {
		setPageNum(pageNum);
		setPageSize(pageSize);
		setTotal(total_count);
		setPages(total_count % pageSize > 0 ? total_count / pageSize + 1 : Math.max(total_count / pageSize, 1));

		setSize(getList().size());

		//setStartRow(getList().get(0).getr);
		//setEndRow(getList().size() > 0 ? getList().size() - 1 : 0);

		setNavigatePages(navigatePages);
		//计算导航页
		final List<Integer> pages = new ArrayList<Integer>();
		for (int i = 1; i <= getPages(); i++) {
			pages.add(i);
		}
		calcNavigatepageNums();

		setIsFirstPage(pageNum == 1);
		setIsLastPage(pageNum == getPages());

		setHasPreviousPage(!isIsFirstPage());
		if (isHasPreviousPage()) {
			setPrePage(pageNum - 1);
		}

		setHasNextPage(!isIsLastPage());
		if (isHasNextPage()) {
			setNextPage(pageNum + 1);
		}

	}

	private static int[] convertIntegers(final List<Integer> integers) {
		final int[] ret = new int[integers.size()];
		final Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	private void calcNavigatepageNums() {
		//当总页数小于或等于导航页码数时
		if (getPages() <= getNavigatePages()) {
			setNavigatepageNums(new int[getPages()]);
			for (int i = 0; i < getPages(); i++) {
				getNavigatepageNums()[i] = i + 1;
			}
		} else { //当总页数大于导航页码数时
			setNavigatepageNums(new int[getNavigatePages()]);
			int startNum = getPageNum() - getNavigatePages() / 2;
			int endNum = getPageNum() + getNavigatePages() / 2;

			if (startNum < 1) {
				startNum = 1;
				//(最前navigatePages页
				for (int i = 0; i < getNavigatePages(); i++) {
					getNavigatepageNums()[i] = startNum++;
				}
			} else if (endNum > getPages()) {
				endNum = getPages();
				//最后navigatePages页
				for (int i = getNavigatePages() - 1; i >= 0; i--) {
					getNavigatepageNums()[i] = endNum--;
				}
			} else {
				//所有中间页
				for (int i = 0; i < getNavigatePages(); i++) {
					getNavigatepageNums()[i] = startNum++;
				}
			}
		}
	}
}
