package hello.spring.service;

import hello.spring.domain.Board;
import hello.spring.dto.BoardDto;
import hello.spring.repository.BoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {

    private BoardRepository boardRepository;

    private static final int BLOCK_PAGE_NUM_COUNT = 5; // 블럭에 존재하는 페이지 번호 수
    private static final int PAGE_POST_COUNT = 4; // 한 페이지에 존재하는 게시글 수

    // Builder 패턴으로 Entity를 Dto로 변환해주는 Method이다.
    // Entity -> Dto 로 변환
    private BoardDto convertEntityToDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .createdDate(board.getCreatedDate())
                .modifiedDate(board.getModifiedDate())
                .build();
    }

    /*
    페이징을 할 수 있도록 구현
    repository의 find() 관련 메서드를 호출할 때
    Pageable 인터페이스를 구현한 Class(PageRequest.of())를 전달하면 Paging을 할 수 있다.
    첫 번째와 두 번째 인자로 page와 size를 전달하고, 세 번째 인자로 정렬 방식을 결정하였다.
    (createdDate 기준으로 오름차순 할 수 있도록 설정한 부분이다.)
    => 반환된 Page 객체의 getContent() 메서드를 호출하면, Entity를 List 형태로 꺼내올 수 있다.
    이를 DTO로 Controller에게 전달하게 된다.
    */

    @Transactional
    public List<BoardDto> getBoardList(Integer pageNum) {
        Page<Board> page = boardRepository.findAll(PageRequest.of(
                pageNum - 1, PAGE_POST_COUNT, Sort.by(Sort.Direction.ASC, "createdDate")));

        List<Board> boardEntities = page.getContent();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (Board board : boardEntities) {
            boardDtoList.add(this.convertEntityToDto(board));
        }

        return boardDtoList;
    }

    /*
    boardRepository의 findById(id) 메서드로 board 게시글 내용을 가져온 뒤
    -> builder() 메서드를 활용하여 boardDTO 객체로 만들고
    -> boadrDTO를 리턴 밸류로 전달해준다.
    */

    @Transactional
    public BoardDto getPost(Long id) {
        // Optional : NPE(NullPointerException) 방지
        Optional<Board> boardWrapper = boardRepository.findById(id);
        Board board = boardWrapper.get();

        BoardDto boardDTO = BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .createdDate(board.getCreatedDate())
                .modifiedDate(board.getModifiedDate())
                .build();

        return boardDTO;
    }

    /*
    boardRepository의 save 메서드를 사용하여 데이터를 저장한다.
    그 뒤에 getter를 활용하여 Id를 받아오고 return 밸류를 전달해준다.
    */

    @Transactional
    public Long savePost(BoardDto boardDto) {
        return boardRepository.save(boardDto.toEntity()).getId();
    }

    // deleteById 메서드로 게시글 삭제

    @Transactional
    public void deletePost(Long id) {
        boardRepository.deleteById(id);
    }

    /*
    사용자가 Front에서 keyword를 검색하면 Controller로부터 keyword를 전달받게 된다.
    이 Keyword가 Entity 내에 있는지 확인하는 Method이다.
    있을 경우, boardEntities를 for loop 돌아서 boardDtoList에 Element를 추가한 뒤
    boardDtoList를 Controller에게 전달해주고, 없을 경우 빈 Array를 전달해준다.
    */
    // 검색 API
    @Transactional
    public List<BoardDto> searchPosts(String keyword) {
        List<Board> boardEntities = boardRepository.findByTitleContaining(keyword);
        List<BoardDto> boardDtoList = new ArrayList<>();

        if (boardEntities.isEmpty()) return boardDtoList;

        for (Board board : boardEntities) {
            boardDtoList.add(this.convertEntityToDto(board));
        }

        return boardDtoList;
    }

    // 전체 게시글 개수를 가져온다.
    // 페이징
    @Transactional
    public Long getBoardCount() {
        return boardRepository.count();
    }

    // - 하나의 Page : 4개의 게시글
    // - 총 5개의 번호를 노출
    // - 번호를 5개 채우지 못하면 (게시글이 20개가 되지 않으면) 존재하는 번호까지만 노출

    public Integer[] getPageList(Integer curPageNum) {
        Integer[] pageList = new Integer[BLOCK_PAGE_NUM_COUNT];

        // 총 게시글 갯수
        Double postsTotalCount = Double.valueOf(this.getBoardCount());

        // 총 게시글 기준으로 계산한 마지막 페이지 번호 계산 (올림으로 계산)
        Integer totalLastPageNum = (int)(Math.ceil((postsTotalCount/PAGE_POST_COUNT)));

        // 현재 페이지를 기준으로 블럭의 마지막 페이지 번호 계산
        Integer blockLastPageNum = (totalLastPageNum > curPageNum + BLOCK_PAGE_NUM_COUNT)
                ? curPageNum + BLOCK_PAGE_NUM_COUNT
                : totalLastPageNum;

        // 페이지 시작 번호 조정
        curPageNum = (curPageNum <= 3) ? 1 : curPageNum - 2;

        // 페이지 번호 할당
        for (int val = curPageNum, idx = 0; val <= blockLastPageNum; val++, idx++) {
            pageList[idx] = val;
        }

        return pageList;
    }
}
