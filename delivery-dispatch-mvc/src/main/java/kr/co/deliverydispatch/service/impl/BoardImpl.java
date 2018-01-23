package kr.co.deliverydispatch.service.impl;

import kr.co.deliverydispatch.service.Board;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("board")
public class BoardImpl implements Board{
    @Override
    public List<Board> selectBoardList(Board board) {
        return null;
    }
}
