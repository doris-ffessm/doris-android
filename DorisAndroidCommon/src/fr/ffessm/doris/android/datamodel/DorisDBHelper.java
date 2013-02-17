/*******************************************************************************
 * Copyright (c) 2012 Vojtisek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Didier Vojtisek - initial API and implementation
 *******************************************************************************/
package fr.ffessm.doris.android.datamodel;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

import fr.ffessm.doris.android.datamodel.associations.*;

/**
 * Context class used to simplify the access to the DAO of the application
 */
public class DorisDBHelper {


	public Dao<Fiche, Integer> ficheDao;
	//public RuntimeExceptionDao<Fiche, Integer> ficheDao;
	public Dao<Participant, Integer> participantDao;
	//public RuntimeExceptionDao<Participant, Integer> participantDao;
	public Dao<PhotoFiche, Integer> photoFicheDao;
	//public RuntimeExceptionDao<PhotoFiche, Integer> photoFicheDao;
	public Dao<PhotoParticipant, Integer> photoParticipantDao;
	//public RuntimeExceptionDao<PhotoParticipant, Integer> photoParticipantDao;
	public Dao<ZoneGeographique, Integer> zoneGeographiqueDao;
	//public RuntimeExceptionDao<ZoneGeographique, Integer> zoneGeographiqueDao;
	public Dao<ZoneObservation, Integer> zoneObservationDao;
	//public RuntimeExceptionDao<ZoneObservation, Integer> zoneObservationDao;
	public Dao<SectionFiche, Integer> sectionFicheDao;
	//public RuntimeExceptionDao<SectionFiche, Integer> sectionFicheDao;
	//public RuntimeExceptionDao<Fiche_Photos, Integer> fiche_PhotosDao;
	public Dao<Fiche_Photos, Integer> fiche_PhotosDao;
	//public RuntimeExceptionDao<Fiches_ZonesGeographiques, Integer> fiches_ZonesGeographiquesDao;
	public Dao<Fiches_ZonesGeographiques, Integer> fiches_ZonesGeographiquesDao;
	//public RuntimeExceptionDao<Fiches_ZonesObservations, Integer> fiches_ZonesObservationsDao;
	public Dao<Fiches_ZonesObservations, Integer> fiches_ZonesObservationsDao;
	//public RuntimeExceptionDao<Participant_Photos, Integer> participant_PhotosDao;
	public Dao<Participant_Photos, Integer> participant_PhotosDao;
	//public RuntimeExceptionDao<Fiches_verificateurs_Participants, Integer> fiches_verificateurs_ParticipantsDao;
	public Dao<Fiches_verificateurs_Participants, Integer> fiches_verificateurs_ParticipantsDao;
	//public RuntimeExceptionDao<Fiche_Sections, Integer> fiche_SectionsDao;
	public Dao<Fiche_Sections, Integer> fiche_SectionsDao;

	
	public DorisDBHelper(){
	}

}
