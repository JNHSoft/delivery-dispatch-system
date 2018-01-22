package kr.co.cntt.core.model;

public abstract class AbstractPagination implements Dto {

	private static final long serialVersionUID = -8335936663741594236L;

	// TODO : modify to be able to config dynamically~~~
	private static final Integer DEFAULT_PAGE_NUMBER = 1;		// 현재 페이지 번호
	private static final Integer DEFAULT_PAGE_SIZE = 10;		// row size
	private static final Integer DEFAULT_NAVIGATE_PAGES = 10;	// page size
	
	public Integer pageNum = DEFAULT_PAGE_NUMBER;
	public Integer pageSize = DEFAULT_PAGE_SIZE;
	public Integer navigatePages = DEFAULT_NAVIGATE_PAGES;
	public String searchWord;
	public String searchType;
	
	public Integer getPageNum() {
		return pageNum;
	}
	public void setPageNum(Integer pageNum) {
		if (pageNum == null) {
			pageNum = DEFAULT_PAGE_NUMBER;
		}
		this.pageNum = pageNum;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		if (pageSize == null) {
			pageSize = DEFAULT_PAGE_SIZE;
	}
		this.pageSize = pageSize;
	}
	
	public void setNavigatePages(Integer navigatePages) {
		this.navigatePages = navigatePages;
	}
	
	public Integer getNavigatePages() {
		return navigatePages;
	}
	
	public String getSearchWord() {
		return searchWord;
	}
	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
}
