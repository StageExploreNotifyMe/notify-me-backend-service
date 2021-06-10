--Venue
INSERT INTO venue_entity (id, name) values (-10, 'Groenplaats');

--Lines
INSERT INTO line_entity (id, description, name, venue_entity_id, number_of_required_people) values (-10, 'The pendel service between the venue and nearby parkings and public transport stops', 'Pendel service', -10, 5);
INSERT INTO line_entity (id, description, name, venue_entity_id, number_of_required_people) values (-11, 'The catering during events', 'Catering', -10, 1);

--Organizations
INSERT INTO organization_entity (id, name) values (-10, 'KdG');
INSERT INTO organization_entity (id, name) values (-11, 'Catering');
INSERT INTO organization_entity (id, name) values (-12, 'Pendel Service');

--Admin
INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values (-10, 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email, password_hash) values (-10, 'Admin', 'Admin', -10, '+32492920000', 'xplorestage21+admin@gmail.com', '$2a$10$Obt2i1i.5mcc5krHb9/G4uZYk4Bfz60PJ16bjSFONqrwUHrTjaZg.');
INSERT INTO user_entity_roles (user_entity_id, roles) values (-10, 4);

--Organization Leaders
INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values (-15, 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email, password_hash) values (-15, 'Organization Leader', '1', -15, '+32492920000', 'xplorestage21+organizationleader1@gmail.com', '$2a$10$jC3zKO/PyOWJPkgoIRetcu60ca0piVjud7enzlmZA2w0SWzHY20W.');
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-10, 1, 0, -10, -15);
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-11, 1, 0, -11, -15);
INSERT INTO user_entity_roles (user_entity_id, roles) values (-15, 1);

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values (-16, 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email, password_hash) values (-16, 'Organization Leader', '2', -16, '+32492920000', 'xplorestage21+organizationleader2@gmail.com', '$2a$10$XuXtlRo3gsmenOALow0whevS3mOUvhx4vibcB.xTGBZ/miDawFXfW');
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-18, 1, 0, -10, -16);
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-19, 1, 0, -11, -16);
INSERT INTO user_entity_roles (user_entity_id, roles) values (-16, 1);

--Members
INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values (-11, 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email, password_hash) values (-11, 'Member', '1', -11, '+32492920000', 'xplorestage21+member1@gmail.com', '$2a$10$Oz1WPhu/jBK6PlrPJyqFUepnwAcFECuPahNabk2hGQxPMTkMIttCO');
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-12, 0, 0, -10, -11);
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-13, 0, 0, -11, -11);
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-14, 0, 0, -12, -11);
INSERT INTO user_entity_roles (user_entity_id, roles) values (-11, 0);

INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values (-12, 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email, password_hash) values (-12, 'Member', '2', -12, '+32492920000', 'xplorestage21+member2@gmail.com', '$2a$10$dOTN4PHOJsdVR.B9jSk6Ueajbkh6FhQIFpZPBFgtCV25dy3gRnVyS');
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-15, 0, 1, -10, -12);
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-16, 0, 1, -11, -12);
INSERT INTO user_organization_entity (id, role, status, organization_entity_id, user_entity_id) values (-17, 0, 1, -12, -12);
INSERT INTO user_entity_roles (user_entity_id, roles) values (-12, 0);

--Venue manager
INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values (-13, 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email, password_hash) values (-13, 'Venue', 'Manager', -13, '+32492920000', 'xplorestage21+venuemanager@gmail.com', '$2a$10$ab9K1tEbkfQxHqP5jEcEdeRTg4/aVQ8Hn4kMCEDr3NzWca7wTMkYK');
INSERT INTO venue_entity_venue_managers (venue_entity_id, venue_managers_id) values (-10, -13);
INSERT INTO user_entity_roles (user_entity_id, roles) values (-13, 2);

--Line manager
INSERT INTO user_preferences_entity (id, normal_channel, urgent_channel) values (-14, 0, 1);
INSERT INTO user_entity (id, firstname, lastname, user_preferences_id, mobile_number, email, password_hash) values (-14, 'Line', 'Manager', -14, '+32492920000', 'xplorestage21+linemanager@gmail.com', '$2a$10$gQw/HyyleUqjeksfY1hHuedjfhTf51QzyeUiTFaaj2lDmpX.pAPPy');
INSERT INTO venue_entity_line_managers (venue_entity_id, line_managers_id) values (-10, -14);
INSERT INTO user_entity_roles (user_entity_id, roles) values (-14, 3);

--Event
INSERT INTO event_entity(id, date, event_status, name, venue_id) values (-10, CURRENT_TIMESTAMP, 1, 'testEvent', -10);
INSERT INTO event_line_entity (id, event_line_status, event_id, line_id, line_manager_id, organization_id) values (-10, 0, -10,-10,-14,-12)