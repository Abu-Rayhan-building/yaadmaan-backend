import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './topic-vote.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITopicVoteDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TopicVoteDetail = (props: ITopicVoteDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { topicVoteEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="topicVoteDetailsHeading">
          <Translate contentKey="yaadbuzzApp.topicVote.detail.title">TopicVote</Translate> [<strong>{topicVoteEntity.id}</strong>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="repetitions">
              <Translate contentKey="yaadbuzzApp.topicVote.repetitions">Repetitions</Translate>
            </span>
          </dt>
          <dd>{topicVoteEntity.repetitions}</dd>
          <dt>
            <Translate contentKey="yaadbuzzApp.topicVote.topic">Topic</Translate>
          </dt>
          <dd>{topicVoteEntity.topic ? topicVoteEntity.topic.id : ''}</dd>
          <dt>
            <Translate contentKey="yaadbuzzApp.topicVote.user">User</Translate>
          </dt>
          <dd>{topicVoteEntity.user ? topicVoteEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/topic-vote" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/topic-vote/${topicVoteEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ topicVote }: IRootState) => ({
  topicVoteEntity: topicVote.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TopicVoteDetail);
