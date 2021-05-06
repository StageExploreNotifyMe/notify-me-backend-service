INSERT INTO venue_entity (id, name) values ('1', 'Groenplaats');
INSERT INTO organization_entity (id, name) values ('1', 'KdG');

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values ('1', 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id) values ('1', 'John', 'Doe', '1');
insert into user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values ('1', 0, 0, '1', '1');

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values ('2', 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id) values ('2', 'Jane', 'Doe', '2');
insert into user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values ('2', 0, 1, '1', '2');

INSERT INTO line_entity (id, description, name, venue_entity_id) values ('1', 'The bar at the main entrance of the venue', 'Main Entrance Bar', '1');
INSERT INTO line_entity (id, description, name, venue_entity_id) values ('2', 'The bar in the VIP area', 'VIP Bar', '1');
INSERT INTO line_entity (id, description, name, venue_entity_id) values ('3', 'People are needed to direct cars to where to park in the parking lot', 'Parking area', '1');

INSERT INTO event_entity(id, date, event_status, name, venue_id) values ('1', CURRENT_TIMESTAMP, 1, 'testEvent', '1');
INSERT INTO event_line_entity (id, event_line_status, event_id, line_id, line_manager_id, organization_id) values ('1', 1, '1','1','1','1')