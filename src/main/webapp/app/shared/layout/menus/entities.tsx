import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <MenuItem icon="asterisk" to="/user-per-department">
      <Translate contentKey="global.menu.entities.userPerDepartment" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/department">
      <Translate contentKey="global.menu.entities.department" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/memory">
      <Translate contentKey="global.menu.entities.memory" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/comment">
      <Translate contentKey="global.menu.entities.comment" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/picture">
      <Translate contentKey="global.menu.entities.picture" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/topic">
      <Translate contentKey="global.menu.entities.topic" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/charateristics">
      <Translate contentKey="global.menu.entities.charateristics" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/memorial">
      <Translate contentKey="global.menu.entities.memorial" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/topic-vote">
      <Translate contentKey="global.menu.entities.topicVote" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/user-extra">
      <Translate contentKey="global.menu.entities.userExtra" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
