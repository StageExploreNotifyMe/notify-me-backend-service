
INSERT INTO organization_entity (id, name) values ('1', 'KdG');

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values ('1', 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id) values ('1', 'John', 'Doe', '1');
insert into user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values ('1', 0, 0, '1', '1');

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values ('2', 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id) values ('2', 'Jane', 'Doe', '2');
insert into user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values ('2', 0, 1, '1', '2');
