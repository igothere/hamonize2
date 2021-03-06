package com.center.hamonize.question;


import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.center.hamonize.cmmn.CmmnMap;
import com.center.hamonize.login.vo.LoginVO;
import com.center.hamonize.notification.NoticeUser;
import com.center.hamonize.notification.NotificationManager;
import com.center.hamonize.question.mapper.QuestionsMapper;
import com.center.hamonize.tag.Tags;
import com.center.hamonize.tag.TagsRepository;
import com.center.hamonize.vote.Vote;
import com.center.hamonize.vote.VoteRepository;
import com.center.hamonize.wiki.Wiki;
import com.center.hamonize.wiki.WikiRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class QuestionsService {

	@Autowired
	QuestionsRepository qr;

	@Autowired
	QuestionsMapper qm;

	@Autowired
	TagsRepository tr;

	@Autowired
	WikiRepository wr;

	@Autowired
	VoteRepository vr;

	@Autowired
	NotificationManager slacknoti;


	public Page<Questions> findAll(Pageable pageable) {
		int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
		pageable = PageRequest.of(page, 5, new Sort(Sort.Direction.DESC, "registerdate"));
		Page<Questions> list = qr.findAllByDeleteat(0, pageable);

		return list;
	}


	// 게시물 목록
	public List<Questions> getList(Questions vo) throws Exception {
		return qm.getList(vo);
	}


	// 게시물 카운트
	public int getListCount(Questions vo) throws Exception {
		return qm.getListCount(vo);
	}


	// 게시물 상세
	public Questions getView(int questionno) throws Exception {
		return qm.getView(questionno);
	}


	// 조회수 증가
	public int updateReanCnt(int questionno) throws Exception {
		return qm.updateReanCnt(questionno);
	}


	public Optional<Questions> findById(int questionno) {
		return qr.findById(questionno);
	}


	public Questions save(Questions vo) throws Exception {
//		Questions nu = new Questions();
//		nu = qm.getView(vo.getUserno());
//		slacknoti.sendNotification(nu);
		
		return qr.save(vo);
	}


	// tag 목록
	public List<Wiki> findAllTag() {
		// int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1); // page는 index 처럼 0부터 시작
		// pageable = PageRequest.of(page, 5,new Sort(Sort.Direction.DESC,"registerdate"));
		List<Wiki> list = wr.findAllBySectionAndDeleteat("t",0);

		return list;
	}


	public List<Tags> tagList() throws Exception {

		return tr.findAll();

	}


	public List<Vote> voteList() throws Exception {

		return vr.findAll();

	}


	public void updateById(Questions vo) throws Exception {
		Optional<Questions> e = qr.findById(vo.getQuestionno());
		CmmnMap param = new CmmnMap();
		param.put("questionno", e.get().getQuestionno());
		param.put("contents", e.get().getContents());
		param.put("section", e.get().getSection());
		param.put("title", e.get().getTitle());
		param.put("deleteat", e.get().getDeleteat());
		param.put("editauth", e.get().getEditauth());
		param.put("readcnt", e.get().getReadcnt());
		param.put("registerdate", e.get().getRegisterdate());
		param.put("tagno", e.get().getTagno());
		param.put("updatedate", e.get().getUpdatedate());
		param.put("userno", e.get().getUserno());
		qm.insertHistory(param);
		
		
		if (e.isPresent()) {
			e.get().setContents(vo.getContents());
			e.get().setTitle(vo.getTitle());
			e.get().setUserno(vo.getUserno());
			e.get().setTagno(vo.getTagno());
			e.get().setReadcnt(e.get().getReadcnt());
			
			if(e.get().getFirstuserno() == vo.getUserno()) {
				e.get().setEditauth(vo.getEditauth());
				
			}
			
			qr.save(e.get());
		}

	}
	
	public int deleteById(int questionno,LoginVO user) throws Exception {
		Optional<Questions> e = qr.findById(questionno);
		int result = 0;
		if (e.isPresent()) {
			if(e.get().getFirstuserno().equals(user.getUserno()) && e.get().getEditauth().equals(0)) {
			e.get().setDeleteat(1);
			qr.save(e.get());
			result = 1;
			}
		}
		return result;

	}


	public Questions updateHistory(Questions vo) throws Exception {

		return qr.save(vo);
	}


	public void updateByIdReadCnt(Questions vo) throws Exception {
		Optional<Questions> e = qr.findById(vo.getQuestionno());
		if (e.isPresent()) {
			e.get().setReadcnt(vo.getReadcnt() + 1);
			qr.save(vo);
		}

	}

	// 내 질문/답변 목록 - 회원
	public List<Questions> getMyList(Questions vo) throws Exception {
		List<Questions> list = null;
		if("A".equals(vo.getSection())) list = qm.getMyAnswerList(vo);
		else if("Q".equals(vo.getSection())) list = qm.getMyQuestionList(vo);
		return list;
	}

	// 내 질문/답변 목록 - 기업
	public List<Questions> getMyListEnter(Questions vo) throws Exception {
		List<Questions> list = null;
		if("A".equals(vo.getSection())) list = qm.getMyAnswerListEnter(vo);
		else if("Q".equals(vo.getSection())) list = qm.getMyQuestionListEnter(vo);
		return list;
	}

	// 게시물 목록
	public List<Questions> getCompQuestionList(String useruuid) throws Exception {
		return qm.getCompQuestionList(useruuid);
	}


	// 게시물 카운트
	public int getCompQuestionListCount(String useruuid) throws Exception {
		return qm.getCompQuestionListCount(useruuid);
	}


	public Questions getAnswerComplete() throws Exception {
		// TODO Auto-generated method stub
		return qm.getAnswerComplete();

	}

}