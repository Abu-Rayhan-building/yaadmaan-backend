package edu.sharif.math.yaadbuzz.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.sharif.math.yaadbuzz.domain.Comment;
import edu.sharif.math.yaadbuzz.repository.CommentRepository;
import edu.sharif.math.yaadbuzz.service.CommentService;
import edu.sharif.math.yaadbuzz.service.DepartmentService;
import edu.sharif.math.yaadbuzz.service.MemoryService;
import edu.sharif.math.yaadbuzz.service.UserPerDepartmentService;
import edu.sharif.math.yaadbuzz.service.UserService;
import edu.sharif.math.yaadbuzz.service.dto.CommentDTO;
import edu.sharif.math.yaadbuzz.service.dto.helpers.CommentWithIdUDTO;
import edu.sharif.math.yaadbuzz.service.mapper.CommentMapper;

/**
 * Service Implementation for managing {@link Comment}.
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final Logger log = LoggerFactory
	    .getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final MemoryService memoryService;
    private final UserPerDepartmentService userPerDepartmentService;

    private final CommentMapper commentMapper;

    public CommentServiceImpl(final CommentRepository commentRepository,
	    final CommentMapper commentMapper,
	    final DepartmentService departmentService,
	    final MemoryService memoryService,
	    final UserPerDepartmentService userPerDepartmentService) {
	this.commentRepository = commentRepository;
	this.memoryService = memoryService;
	this.userPerDepartmentService = userPerDepartmentService;
	this.commentMapper = commentMapper;
    }

    @Override
    public boolean currentuserHasCreateAccess(final Long memid) {
	return this.memoryService.currentuserHasGetAccess(memid);
    }

    @Override
    public boolean currentuserHasGetAccess(final Long id) {
	final var comment = this.commentRepository.getOne(id);
	final var mem = comment.getMemory();
	return this.memoryService.currentuserHasGetAccess(mem.getId());
    }

    @Override
    public boolean currentuserHasUpdateAccess(final Long id) {
	final var comment = this.commentRepository.getOne(id);

	final var currentUserId = this.userPerDepartmentService
		.getCurrentUserInDep(
			comment.getMemory().getDepartment().getId());
	return comment.getWriter().getId().equals(currentUserId);
    }

    @Override
    public void delete(final Long id) {
	this.log.debug("Request to delete Comment : {}", id);
	this.commentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> findAll(final Pageable pageable) {
	this.log.debug("Request to get all Comments");
	return this.commentRepository.findAll(pageable)
		.map(this.commentMapper::toDto);
    }

    @Override
    public Page<CommentDTO> findAllForMemory(final Long memid,
	    final Pageable pageable) {
	this.log.debug("Request to get all Comments for post");
	return this.commentRepository.findAllForMemory(memid, pageable)
		.map(this.commentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDTO> findOne(final Long id) {
	this.log.debug("Request to get Comment : {}", id);
	return this.commentRepository.findById(id)
		.map(this.commentMapper::toDto);
    }

    @Override
    public CommentDTO save(final CommentDTO commentDTO) {
	this.log.debug("Request to save Comment : {}", commentDTO);
	if (commentDTO.getId() != null) {
	    final var com = this.findOne(commentDTO.getId());
	    if (!com.get().getMemoryId().equals(commentDTO.getMemoryId())) {
		throw new RuntimeException("bad req in CommentDTO save");
	    }
	    if (!com.get().getWriterId().equals(commentDTO.getWriterId())) {
		throw new RuntimeException("bad req in CommentDTO save");
	    }
	}
	Comment comment = this.commentMapper.toEntity(commentDTO);
	comment = this.commentRepository.save(comment);
	return this.commentMapper.toDto(comment);
    }

    @Override
    public CommentDTO save(final CommentWithIdUDTO commentUpdateReqDTO) {
	this.log.debug("Request to save Comment : {}", commentUpdateReqDTO);
	var com = this.findOne(commentUpdateReqDTO.getId()).get();
	com.setText(commentUpdateReqDTO.getText());
	com.setPictures(commentUpdateReqDTO.getPictures());
	Comment comment = this.commentMapper.toEntity(com);
	comment = this.commentRepository.save(comment);
	return this.commentMapper.toDto(comment);
    }
}
