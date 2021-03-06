package edu.sharif.math.yaadbuzz.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.sharif.math.yaadbuzz.IntegrationTest;
import edu.sharif.math.yaadbuzz.domain.Comment;
import edu.sharif.math.yaadbuzz.domain.Memory;
import edu.sharif.math.yaadbuzz.domain.Picture;
import edu.sharif.math.yaadbuzz.domain.UserPerDepartment;
import edu.sharif.math.yaadbuzz.repository.CommentRepository;
import edu.sharif.math.yaadbuzz.service.CommentQueryService;
import edu.sharif.math.yaadbuzz.service.dto.CommentCriteria;
import edu.sharif.math.yaadbuzz.service.dto.CommentDTO;
import edu.sharif.math.yaadbuzz.service.mapper.CommentMapper;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CommentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommentResourceIT {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentQueryService commentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommentMockMvc;

    private Comment comment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createEntity(EntityManager em) {
        Comment comment = new Comment().text(DEFAULT_TEXT);
        // Add required entity
        Picture picture;
        if (TestUtil.findAll(em, Picture.class).isEmpty()) {
            picture = PictureResourceIT.createEntity(em);
            em.persist(picture);
            em.flush();
        } else {
            picture = TestUtil.findAll(em, Picture.class).get(0);
        }
        comment.getPictures().add(picture);
        // Add required entity
        UserPerDepartment userPerDepartment;
        if (TestUtil.findAll(em, UserPerDepartment.class).isEmpty()) {
            userPerDepartment = UserPerDepartmentResourceIT.createEntity(em);
            em.persist(userPerDepartment);
            em.flush();
        } else {
            userPerDepartment = TestUtil.findAll(em, UserPerDepartment.class).get(0);
        }
        comment.setWriter(userPerDepartment);
        // Add required entity
        Memory memory;
        if (TestUtil.findAll(em, Memory.class).isEmpty()) {
            memory = MemoryResourceIT.createEntity(em);
            em.persist(memory);
            em.flush();
        } else {
            memory = TestUtil.findAll(em, Memory.class).get(0);
        }
        comment.setMemory(memory);
        return comment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createUpdatedEntity(EntityManager em) {
        Comment comment = new Comment().text(UPDATED_TEXT);
        // Add required entity
        Picture picture;
        if (TestUtil.findAll(em, Picture.class).isEmpty()) {
            picture = PictureResourceIT.createUpdatedEntity(em);
            em.persist(picture);
            em.flush();
        } else {
            picture = TestUtil.findAll(em, Picture.class).get(0);
        }
        comment.getPictures().add(picture);
        // Add required entity
        UserPerDepartment userPerDepartment;
        if (TestUtil.findAll(em, UserPerDepartment.class).isEmpty()) {
            userPerDepartment = UserPerDepartmentResourceIT.createUpdatedEntity(em);
            em.persist(userPerDepartment);
            em.flush();
        } else {
            userPerDepartment = TestUtil.findAll(em, UserPerDepartment.class).get(0);
        }
        comment.setWriter(userPerDepartment);
        // Add required entity
        Memory memory;
        if (TestUtil.findAll(em, Memory.class).isEmpty()) {
            memory = MemoryResourceIT.createUpdatedEntity(em);
            em.persist(memory);
            em.flush();
        } else {
            memory = TestUtil.findAll(em, Memory.class).get(0);
        }
        comment.setMemory(memory);
        return comment;
    }

    @BeforeEach
    public void initTest() {
        comment = createEntity(em);
    }

    @Test
    @Transactional
    void createComment() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();
        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);
        restCommentMockMvc
            .perform(post("/api/comments").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isCreated());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getText()).isEqualTo(DEFAULT_TEXT);
    }

    @Test
    @Transactional
    void createCommentWithExistingId() throws Exception {
        // Create the Comment with an existing ID
        comment.setId(1L);
        CommentDTO commentDTO = commentMapper.toDto(comment);

        int databaseSizeBeforeCreate = commentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommentMockMvc
            .perform(post("/api/comments").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setText(null);

        // Create the Comment, which fails.
        CommentDTO commentDTO = commentMapper.toDto(comment);

        restCommentMockMvc
            .perform(post("/api/comments").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isBadRequest());

        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllComments() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList
        restCommentMockMvc
            .perform(get("/api/comments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)));
    }

    @Test
    @Transactional
    void getComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get the comment
        restCommentMockMvc
            .perform(get("/api/comments/{id}", comment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(comment.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT));
    }

    @Test
    @Transactional
    void getCommentsByIdFiltering() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        Long id = comment.getId();

        defaultCommentShouldBeFound("id.equals=" + id);
        defaultCommentShouldNotBeFound("id.notEquals=" + id);

        defaultCommentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCommentShouldNotBeFound("id.greaterThan=" + id);

        defaultCommentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCommentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCommentsByTextIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where text equals to DEFAULT_TEXT
        defaultCommentShouldBeFound("text.equals=" + DEFAULT_TEXT);

        // Get all the commentList where text equals to UPDATED_TEXT
        defaultCommentShouldNotBeFound("text.equals=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllCommentsByTextIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where text not equals to DEFAULT_TEXT
        defaultCommentShouldNotBeFound("text.notEquals=" + DEFAULT_TEXT);

        // Get all the commentList where text not equals to UPDATED_TEXT
        defaultCommentShouldBeFound("text.notEquals=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllCommentsByTextIsInShouldWork() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where text in DEFAULT_TEXT or UPDATED_TEXT
        defaultCommentShouldBeFound("text.in=" + DEFAULT_TEXT + "," + UPDATED_TEXT);

        // Get all the commentList where text equals to UPDATED_TEXT
        defaultCommentShouldNotBeFound("text.in=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllCommentsByTextIsNullOrNotNull() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where text is not null
        defaultCommentShouldBeFound("text.specified=true");

        // Get all the commentList where text is null
        defaultCommentShouldNotBeFound("text.specified=false");
    }

    @Test
    @Transactional
    void getAllCommentsByTextContainsSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where text contains DEFAULT_TEXT
        defaultCommentShouldBeFound("text.contains=" + DEFAULT_TEXT);

        // Get all the commentList where text contains UPDATED_TEXT
        defaultCommentShouldNotBeFound("text.contains=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllCommentsByTextNotContainsSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get all the commentList where text does not contain DEFAULT_TEXT
        defaultCommentShouldNotBeFound("text.doesNotContain=" + DEFAULT_TEXT);

        // Get all the commentList where text does not contain UPDATED_TEXT
        defaultCommentShouldBeFound("text.doesNotContain=" + UPDATED_TEXT);
    }

    @Test
    @Transactional
    void getAllCommentsByPicturesIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        Picture pictures = PictureResourceIT.createEntity(em);
        em.persist(pictures);
        em.flush();
        comment.addPictures(pictures);
        commentRepository.saveAndFlush(comment);
        Long picturesId = pictures.getId();

        // Get all the commentList where pictures equals to picturesId
        defaultCommentShouldBeFound("picturesId.equals=" + picturesId);

        // Get all the commentList where pictures equals to picturesId + 1
        defaultCommentShouldNotBeFound("picturesId.equals=" + (picturesId + 1));
    }

    @Test
    @Transactional
    void getAllCommentsByWriterIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        UserPerDepartment writer = UserPerDepartmentResourceIT.createEntity(em);
        em.persist(writer);
        em.flush();
        comment.setWriter(writer);
        commentRepository.saveAndFlush(comment);
        Long writerId = writer.getId();

        // Get all the commentList where writer equals to writerId
        defaultCommentShouldBeFound("writerId.equals=" + writerId);

        // Get all the commentList where writer equals to writerId + 1
        defaultCommentShouldNotBeFound("writerId.equals=" + (writerId + 1));
    }

    @Test
    @Transactional
    void getAllCommentsByMemoryIsEqualToSomething() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        Memory memory = MemoryResourceIT.createEntity(em);
        em.persist(memory);
        em.flush();
        comment.setMemory(memory);
        commentRepository.saveAndFlush(comment);
        Long memoryId = memory.getId();

        // Get all the commentList where memory equals to memoryId
        defaultCommentShouldBeFound("memoryId.equals=" + memoryId);

        // Get all the commentList where memory equals to memoryId + 1
        defaultCommentShouldNotBeFound("memoryId.equals=" + (memoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommentShouldBeFound(String filter) throws Exception {
        restCommentMockMvc
            .perform(get("/api/comments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)));

        // Check, that the count call also returns 1
        restCommentMockMvc
            .perform(get("/api/comments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommentShouldNotBeFound(String filter) throws Exception {
        restCommentMockMvc
            .perform(get("/api/comments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommentMockMvc
            .perform(get("/api/comments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingComment() throws Exception {
        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // Update the comment
        Comment updatedComment = commentRepository.findById(comment.getId()).get();
        // Disconnect from session so that the updates on updatedComment are not directly saved in db
        em.detach(updatedComment);
        updatedComment.text(UPDATED_TEXT);
        CommentDTO commentDTO = commentMapper.toDto(updatedComment);

        restCommentMockMvc
            .perform(put("/api/comments").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void updateNonExistingComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentMockMvc
            .perform(put("/api/comments").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(commentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // Update the comment using partial update
        Comment partialUpdatedComment = new Comment();
        partialUpdatedComment.setId(comment.getId());

        partialUpdatedComment.text(UPDATED_TEXT);

        restCommentMockMvc
            .perform(
                patch("/api/comments")
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComment))
            )
            .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void fullUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // Update the comment using partial update
        Comment partialUpdatedComment = new Comment();
        partialUpdatedComment.setId(comment.getId());

        partialUpdatedComment.text(UPDATED_TEXT);

        restCommentMockMvc
            .perform(
                patch("/api/comments")
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComment))
            )
            .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(commentList.size() - 1);
        assertThat(testComment.getText()).isEqualTo(UPDATED_TEXT);
    }

    @Test
    @Transactional
    void partialUpdateCommentShouldThrown() throws Exception {
        // Update the comment without id should throw
        Comment partialUpdatedComment = new Comment();

        restCommentMockMvc
            .perform(
                patch("/api/comments")
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedComment))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void deleteComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        int databaseSizeBeforeDelete = commentRepository.findAll().size();

        // Delete the comment
        restCommentMockMvc
            .perform(delete("/api/comments/{id}", comment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
