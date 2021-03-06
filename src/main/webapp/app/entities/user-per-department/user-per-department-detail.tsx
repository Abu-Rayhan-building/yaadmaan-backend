import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './user-per-department.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IUserPerDepartmentDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const UserPerDepartmentDetail = (props: IUserPerDepartmentDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { userPerDepartmentEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userPerDepartmentDetailsHeading">
          <Translate contentKey="yaadbuzzApp.userPerDepartment.detail.title">UserPerDepartment</Translate> [
          <strong>{userPerDepartmentEntity.id}</strong>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="nickname">
              <Translate contentKey="yaadbuzzApp.userPerDepartment.nickname">Nickname</Translate>
            </span>
          </dt>
          <dd>{userPerDepartmentEntity.nickname}</dd>
          <dt>
            <span id="bio">
              <Translate contentKey="yaadbuzzApp.userPerDepartment.bio">Bio</Translate>
            </span>
          </dt>
          <dd>{userPerDepartmentEntity.bio}</dd>
          <dt>
            <Translate contentKey="yaadbuzzApp.userPerDepartment.avatar">Avatar</Translate>
          </dt>
          <dd>{userPerDepartmentEntity.avatar ? userPerDepartmentEntity.avatar.id : ''}</dd>
          <dt>
            <Translate contentKey="yaadbuzzApp.userPerDepartment.realUser">Real User</Translate>
          </dt>
          <dd>{userPerDepartmentEntity.realUser ? userPerDepartmentEntity.realUser.id : ''}</dd>
          <dt>
            <Translate contentKey="yaadbuzzApp.userPerDepartment.department">Department</Translate>
          </dt>
          <dd>{userPerDepartmentEntity.department ? userPerDepartmentEntity.department.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/user-per-department" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-per-department/${userPerDepartmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ userPerDepartment }: IRootState) => ({
  userPerDepartmentEntity: userPerDepartment.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(UserPerDepartmentDetail);
