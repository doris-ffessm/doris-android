/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
 *           Didier Vojtisek <dvojtise@gmail.com>
 * *********************************************************************

Ce logiciel est un programme informatique servant à afficher de manière 
ergonomique sur un terminal Android les fiches du site : doris.ffessm.fr. 

Les images, logos et textes restent la propriété de leurs auteurs, cf. : 
doris.ffessm.fr.

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et
respectant les principes de diffusion des logiciels libres. Vous pouvez
utiliser, modifier et/ou redistribuer ce programme sous les conditions
de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA 
sur le site "http://www.cecill.info".

En contrepartie de l'accessibilité au code source et des droits de copie,
de modification et de redistribution accordés par cette licence, il n'est
offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
seule une responsabilité restreinte pèse sur l'auteur du programme,  le
titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques
associés au chargement,  à l'utilisation,  à la modification et/ou au
développement et à la reproduction du logiciel par l'utilisateur étant 
donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
manipuler et qui le réserve donc à des développeurs et des professionnels
avertis possédant  des  connaissances  informatiques approfondies.  Les
utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
logiciel à leurs besoins dans des conditions permettant d'assurer la
sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
pris connaissance de la licence CeCILL-B, et que vous en avez accepté les
termes.
* ********************************************************************* */
package fr.ffessm.doris.android.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.DetailsFiche_ElementViewActivity;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Jeu;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;
import fr.ffessm.doris.android.tools.Reseau_Outils;

import static java.lang.Math.random;

public class JeuxQuestion_CustomViewFragment extends Fragment implements OnItemClickListener
{

	private static final String LOG_TAG = JeuxQuestion_CustomViewFragment.class.getCanonicalName();

    final static String ARG_JEUX = "jeux";

    BoutonSuivantListener boutonSuivantCallback;

    private ImageView ivQuestionImage;
    private TextView tvQuestionLibelle;
    private TextView tvTitreTexte;
    private ImageView ivTitreIcone;
    private TextView tvTitreIconeLabel;
    private ImageView ivBtnSuivant;
    private ImageView ivBtnAffFiche;

    private Photos_Outils photosOutils;
    private Reseau_Outils reseauOutils;

    public interface BoutonSuivantListener {
        public void onBoutonSuivant();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "onAttach() - Début");

        try {
            boutonSuivantCallback = (JeuxQuestion_CustomViewFragment.BoutonSuivantListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BoutonSuivantListener");
        }

        Log.d(LOG_TAG, "onAttach() - Fin");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView() - Début");

        View view = inflater.inflate(R.layout.jeux_questionview_fragment, container, false);

        ivQuestionImage = (ImageView) view.findViewById(R.id.jeu_question_image);
        tvQuestionLibelle = (TextView) view.findViewById(R.id.jeu_question_libelle);
        tvTitreTexte =  (TextView) view.findViewById(R.id.jeu_question_titre_texte);
        ivTitreIcone =  (ImageView) view.findViewById(R.id.jeu_question_titre_icone);
        tvTitreIconeLabel =  (TextView) view.findViewById(R.id.jeu_question_titre_icone_label);
        ivBtnSuivant =  (ImageView) view.findViewById(R.id.jeu_question_btn_suivant);
        ivBtnAffFiche =  (ImageView) view.findViewById(R.id.jeu_question_image_icone);

        reseauOutils = new Reseau_Outils(getActivity());

        Log.d(LOG_TAG, "onCreateView() - containerId : "+this.getId());
        Log.d(LOG_TAG, "onCreateView() - containerTag : "+this.getTag());

        Log.d(LOG_TAG, "onCreateView() - Fin");
        return view;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart() - Début");
        super.onStart();

        Log.d(LOG_TAG, "onStart() - containerId : "+this.getId());
        Log.d(LOG_TAG, "onStart() - containerTag : "+this.getTag());

        Log.d(LOG_TAG, "onStart() - Fin");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState() - Début");
        super.onSaveInstanceState(outState);

        Log.d(LOG_TAG, "onSaveInstanceState() - Fin");
    }

    public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
        Log.d(LOG_TAG, "onItemClick "+view);

    }

    public void setQuestionImage(int imgId) {
        ivQuestionImage.setImageResource(imgId);
    }
    public void setQuestionImage(String imageURL, ImageType imageType) {
        Log.d(LOG_TAG, "setImage() - Début");
        Log.d(LOG_TAG, "setImage() - imageURL : "+imageURL);
        Log.d(LOG_TAG, "setImage() - imageType : "+imageType);

        if (getPhotosOutils().isAvailableInFolderPhoto(imageURL, imageType)) {

            String photoNom = imageURL.substring(imageURL.lastIndexOf('/') + 1);
            if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - photoNom : "+ photoNom );

            try {
                Picasso.with(getActivity()).load(getPhotosOutils().getPhotoFile(photoNom, imageType))
                        .fit()
                        .centerInside()
                        .into(ivQuestionImage);
            } catch (IOException e) {
                Log.d(LOG_TAG, "setImage() - IOException : "+e);
            }
        } else {
            // pas préchargée en local pour l'instant, cherche sur internet
            Log.d(LOG_TAG, "setImage() -  pas préchargée en local pour l'instant, cherche sur internet");
            if (reseauOutils.isTelechargementsModeConnectePossible()) {
                String urlPhoto = Constants.IMAGE_BASE_URL + "/" + imageURL;
                Log.d(LOG_TAG, "setImage() - urlPhoto : "+urlPhoto);
                Picasso.with(getActivity())
                        .load(urlPhoto.replace(" ", "%20"))
                        .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par defaut pour commencer
                        .error(R.drawable.doris_icone_doris_large_pas_connecte)
                        .fit()
                        .centerInside()
                        .into(ivQuestionImage);
            } else {
                ivQuestionImage.setImageResource(R.drawable.doris_icone_doris_large_pas_connecte);
            }
        }
        Log.d(LOG_TAG, "setImage() - Fin");
    }

    public void setQuestionLibelle(String libelle) {
        tvQuestionLibelle.setText(libelle);
    }

    public void setTitreTexte(String titreTexte) {
        tvTitreTexte.setText(titreTexte);
    }

    public void setTitreIcone(int imgId) {
        ivTitreIcone.setImageResource(imgId);
    }
    public void setTitreIcone(Jeu.JeuRef jeuId) {
        String jeux_icone[] = getActivity().getResources().getStringArray(R.array.jeux_titre_icone);
        ivTitreIcone.setImageResource(getActivity().getResources().getIdentifier(jeux_icone[jeuId.ordinal()],"drawable", getActivity().getPackageName()));
    }

    public void setTitreIconeLabel(String iconeLabel) {
        tvTitreIconeLabel.setText(iconeLabel);
    }

    /* * * * Fonctions du Jeu * * * */

    /* Création de la liste des Jeux */
    public void createListeJeuxViews(){
        Log.d(LOG_TAG, "createListeJeuxViews() - Début");

        setQuestionImage(R.drawable.ic_action_jeux);
        setQuestionLibelle("");

        setTitreTexte(getString(R.string.jeu_question_choix_accueil));
        setTitreIcone(R.drawable.app_ic_launcher);
        resetTvTitreIconeLabel();

        ivBtnAffFiche.setVisibility(View.GONE);

        Log.d(LOG_TAG, "createListeJeuxViews() - Fin");
    }

    /* Création de la liste de Choix de la Zone Géographique */
    public void createListeZonesGeographiquesViews(){
        Log.d(LOG_TAG, "createListeZonesGeographiquesViews() - Début");

        setQuestionImage(R.drawable.ic_action_jeux);
        setQuestionLibelle("");

        tvTitreTexte.setText(getString(R.string.jeu_question_choix_zone_geographique));
        setTitreIcone(DorisApplicationContext.getInstance().jeuSelectionne);
        resetTvTitreIconeLabel();

        ivBtnAffFiche.setVisibility(View.GONE);

        Log.d(LOG_TAG, "createListeZonesGeographiquesViews() - Fin");
    }

    /* Création de la liste des Niveaux */
    public void createListeNiveauxViews(){
        Log.d(LOG_TAG, "createListeNiveauxViews() - Début");

        setQuestionImage(R.drawable.ic_action_jeux);
        setQuestionLibelle("");

        tvTitreTexte.setText(getString(R.string.jeu_question_choix_niveau));
        setTitreIcone(DorisApplicationContext.getInstance().jeuSelectionne);
        resetTvTitreIconeLabel();

        ivBtnAffFiche.setVisibility(View.GONE);

        Log.d(LOG_TAG, "createListeNiveauxViews() - Fin");
    }

    /* Création de la liste des Réponses */
    public void createListeReponsesViews(Jeu.Niveau niveau, Fiche fiche, Classification classification){
        Log.d(LOG_TAG, "createListeReponsesViews() - Début");
        Log.d(LOG_TAG, "createListeReponsesViews() - niveau : "+niveau.name());
        Log.d(LOG_TAG, "createListeReponsesViews() - fiche : "+fiche.getNomCommun());

        if (DorisApplicationContext.getInstance().reponseOK == false) {
            desactivationBoutonSuivant();
        }

        tvTitreTexte.setText("Quel(le) : "+classification.getNiveau().replaceAll("\\{\\{[^\\}]*\\}\\}",""));

        boutonAffichageFiche(fiche);

        Log.d(LOG_TAG, "createListeReponsesViews() - Fin");
    }


    /* Activation Bouton Suivant */
    public void activationBoutonSuivant(final Jeu.JeuRef jeuSelectionne, final Jeu.Niveau niveau){
        ivBtnSuivant.setColorFilter(null);
        ivBtnSuivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "activationBoutonSuivant() - onClick()");

                //createListeReponsesViews(DorisApplicationContext.getInstance().jeuNiveauSelectionne, fiche, classification);
                if(boutonSuivantCallback != null)
                    boutonSuivantCallback.onBoutonSuivant();

            }
        });
        ivBtnSuivant.setClickable(true);
    }
    /* DésActivation Bouton Suivant */
    public void desactivationBoutonSuivant(){
        ivBtnSuivant.setVisibility(View.VISIBLE);
        ivBtnSuivant.setColorFilter(0xff222222, PorterDuff.Mode.SRC_ATOP);

        ivBtnSuivant.setClickable(false);
    }

    /* Activation Bouton Suivant */
    public void boutonAffichageFiche(final Fiche fiche){
        ivBtnAffFiche.setVisibility(View.VISIBLE);
        ivBtnAffFiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "boutonAffichageFiche() - onClick()");

                Intent toDetailView = new Intent(getActivity(), DetailsFiche_ElementViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("ficheNumero", fiche.getNumeroFiche());
                bundle.putInt("ficheId", fiche.getId() );
                toDetailView.putExtras(bundle);
                getActivity().startActivity(toDetailView);
            }
        });
        ivBtnSuivant.setClickable(true);
    }

    /* MaJ Libellé de l'icone */
    public void setTvTitreIconeLabel(String label){
        tvTitreIconeLabel.setVisibility(View.VISIBLE);
        tvTitreIconeLabel.setText(label);
    }
    public void resetTvTitreIconeLabel(){
        tvTitreIconeLabel.setVisibility(View.GONE);
        tvTitreIconeLabel.setText("");
        ivBtnSuivant.setVisibility(View.GONE);
    }

    private Photos_Outils getPhotosOutils(){
        if(photosOutils == null) photosOutils = new Photos_Outils(getActivity());
        return photosOutils;
    }

}