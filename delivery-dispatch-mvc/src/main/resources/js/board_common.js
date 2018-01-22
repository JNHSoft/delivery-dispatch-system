//<![CDATA[

	// page parameter
	var PageParam = {
		cate : ""
	};
	
	// page functions
	var PageFunction = (function(pf){
		
		pf.init = function(){
			// 검색창 Enter키 입력
			$("#searchWord").keyup(function(e){
				if(e.keyCode == 13)
					$("#searchBtn").click();
			});
		}
		
		// 리스트 정보 조회
		pf.getList = function(cate){
			var url_param = "?searchWord=" + $("#searchWord").val() + "&searchType=" + $("select[name=searchType]").val();
			
			$("#boardList").load("/board_frag/"+ cate +"/"+ cate +"_table" + url_param);
		}
		
		// 게시글 등록
		pf.doInsert = function(cate){
			cntt.ajax.post('/board/'+ cate, $("#board_form").serializeObject(), function(response){
				// 성공 후처리
				if(response){
					console.log(response);
					alert("등록 되었습니다.");
					//location.href="/board/"+ cate;
				}
			})
		}
		
		// 게시글 수정
		pf.doUpdate = function(cate, seq){
			cntt.ajax.post('/board/'+ cate +"/"+ seq, $("#board_form").serializeObject(), function(response){
				// 성공 후처리
				if(response){
					alert("수정 되었습니다.");
					location.href="/board/"+ cate;
				}
			}, {type:'put'})
		}
		
		// 게시글 삭제
		pf.doDelete = function(cate, seq){
			cntt.ajax.post('/board/'+ cate +"/"+ seq, null, function(response){
				// 성공 후처리
				if(response){
					alert("삭제 되었습니다.");
					location.href="/board/"+ cate;
				}
			}, {type:'delete'})
		}
		
		return pf;
	}(window.pf || {}));

//]]>