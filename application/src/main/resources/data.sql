INSERT INTO venue_entity (id, name) values ('1', 'Groenplaats');
INSERT INTO organization_entity (id, name) values ('1', 'KdG');

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values ('1', 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email) values ('1', 'John', 'Doe', '1', '+32492920000', 'gerd.goemans@xplore.be');
insert into user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values ('1', 1, 0, '1', '1');

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values ('2', 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email) values ('2', 'Jane', 'Doe', '2', '+32492920000', 'gerd.goemans@xplore.be');
insert into user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values ('2', 0, 1, '1', '2');

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values ('3', 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email) values ('3', 'John', 'Smith', '3', '+32492920000', 'gerd.goemans@xplore.be');
INSERT INTO venue_entity_line_managers (venue_entity_id, line_managers_id) values ('1', '3');

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values ('4', 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email) values ('4', 'Jane', 'Smith', '4', '+32492920000', 'gerd.goemans@xplore.be');
INSERT INTO venue_entity_venue_managers (venue_entity_id, venue_managers_id) values ('1', '4');

INSERT INTO line_entity (id, description, name, venue_entity_id, number_of_required_people) values ('1', 'The pendel service between the venue and nearby parkings and public transport stops', 'Pendel service', '1', 5);
INSERT INTO line_entity (id, description, name, venue_entity_id, number_of_required_people) values ('2', 'The catering during events', 'Catering', '1', 1);

INSERT INTO event_entity(id, date, event_status, name, venue_id) values ('1', CURRENT_TIMESTAMP, 1, 'testEvent', '1');
INSERT INTO event_line_entity (id, event_line_status, event_id, line_id, line_manager_id, organization_id) values ('1', 0, '1','1','3','1')