
INSERT INTO organization (id, name) values ('1', 'KdG');

INSERT INTO user_preferences (id, normal_channel, urgent_channel) values ('1', 0, 1);
INSERT INTO user (id, firstname, lastname, user_preferences_id) values ('1', 'test', 'user', '1');
-- insert into user_organization (id, role, status, organization_id, user_id) values ('1', 0, 1, '1', '1');
-- insert into user_organization (id, role, status, organization_id, user_id) values ('3', 0, 1, '1', '1');

INSERT INTO user_preferences (id, normal_channel, urgent_channel) values ('2', 0, 1);
INSERT INTO user (id, firstname, lastname, user_preferences_id) values ('2', 'test', 'user 2', '2');
-- insert into user_organization (id, role, status, organization_id, user_id) values ('2', 0, 1, '1', '2');
-- insert into user_organization (id, role, status, organization_id, user_id) values ('4', 0, 1, '1', '2');


