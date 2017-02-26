/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2015 - FFESSM
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

package fr.ffessm.doris.prefetch;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.android.sitedoris.Constants.FileHtmlKind;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.prefetch.PrefetchDorisWebSite.ActionKind;


public class PrefetchGroupes {


	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchGroupes.class);
	
	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;
	
	private ActionKind action;
	private int nbMaxFichesATraiter;
    private int nbFichesParRequetes;
	
	public List<Groupe> listeGroupes;
	private Groupe groupeMaj;
	
	public PrefetchGroupes(DorisDBHelper dbContext, ConnectionSource connectionSource, ActionKind action, int nbMaxFichesATraiter) {
		this.dbContext = dbContext;
		this.connectionSource = connectionSource;
		this.action = action;
		this.nbMaxFichesATraiter = nbMaxFichesATraiter;
	}

    public PrefetchGroupes(DorisDBHelper dbContext, ConnectionSource connectionSource, ActionKind action, int nbMaxFichesATraiter, int nbFichesParRequetes) {
        this.dbContext = dbContext;
        this.connectionSource = connectionSource;
        this.action = action;
        this.nbMaxFichesATraiter = nbMaxFichesATraiter;
        this.nbFichesParRequetes = nbFichesParRequetes;
    }

    public int prefetchV4() {
        log.debug("prefetchV4() - début");

        List<Groupe> listeGroupes = new ArrayList<Groupe>();

        //public Groupe(int numeroGroupe, int numeroSousGroupe, java.lang.String nomGroupe, java.lang.String descriptionGroupe, java.lang.String cleURLImage, java.lang.String nomImage, java.lang.String descriptionDetailleeGroupe)
        // Cette 1ère entrée ne sert à rien : Juste pour que ça marche, mais les numéros des groupes père commencent à 1 alors que les entrées dans la liste à 0
        // en créant un 1er enregistrement bidon, on tombe juste :-\
        listeGroupes.add(new Groupe(0,0,"","","","","", null));

        listeGroupes.add(new Groupe(0,0,"racine","Les grands groupes","","","", null));
        listeGroupes.add(new Groupe(0,0,"PROCARYOTES","","images/pucecarre.gif","","", listeGroupes.get(1)));
        listeGroupes.add(new Groupe(1,0,"Procaryotes","Bactéries et Cyanobactéries","gestionenligne/images_groupe/1.gif","","On nomme « procaryotes » (terme n'ayant plus de valeur systématique) des organismes unicellulaires dépourvus de noyau. Ce sont les {{g}}bactéries{{/g}} au sens large (Archées + Eubactéries), et en particulier les {{g}}cyanobactéries{{/g}} (encore appelées algues bleues), bactéries photosynthétiques parfois visibles en plongée sous forme d'un voile filamenteux.", listeGroupes.get(2)));
        listeGroupes.add(new Groupe(0,0,"VEGETAUX","","images/pucecarre.gif","","", listeGroupes.get(1)));
        listeGroupes.add(new Groupe(0,0,"Algues","","gestionenligne/images_groupe/2.gif","","", listeGroupes.get(4)));
        listeGroupes.add(new Groupe(2,0,"Rhodophycées","Algues rouges","gestionenligne/images_groupe/2.gif","","Les algues sont des végétaux chlorophylliens unicellulaires ou pluricellulaires constitués d'un thalle (appareil végétatif) peu différencié. Chez les algues pluricellulaires il peut avoir des formes très diverses plus ou moins ramifiées.Le terme de Rhodophycées, ou algues rouges, est utilisé pour désigner les algues possédant en plus des pigments les plus communs (chlorophylles…) des pigments rouges et des pigments bleus.", listeGroupes.get(5)));
        listeGroupes.add(new Groupe(2,72,"Thalles érigés","","gestionenligne/images_sousgroupe/72.gif","","Nous avons choisi de regrouper ici les espèces d'algues rouges dont le thalle présente un port érigé, que leur texture soit souple ou calcifiée.{{n/}}[NB : ce regroupement est purement pratique et ne représente pas forcément un taxon homogène.]", listeGroupes.get(6)));
        listeGroupes.add(new Groupe(2,73,"Thalles encroûtants","","gestionenligne/images_sousgroupe/73.gif","","Nous avons choisi de regrouper ici les espèces d'algues rouges dont le thalle présente un port encroûtant, que leur texture soit souple ou calcifiée.{{n/}}[NB : ce regroupement est purement pratique et ne représente pas forcément un taxon homogène.]", listeGroupes.get(6)));
        listeGroupes.add(new Groupe(3,0,"Chlorophycées","Algues vertes","gestionenligne/images_groupe/3.gif","","Les algues sont des végétaux chlorophylliens unicellulaires ou pluricellulaires constitués d'un thalle (appareil végétatif) peu différencié. Chez les algues pluricellulaires il peut avoir des formes très diverses plus ou moins ramifiées.Le terme de Chlorophycées, ou algues vertes, est utilisé pour désigner les algues dont les chlorophylles sont les pigments dominants.", listeGroupes.get(5)));
        listeGroupes.add(new Groupe(4,0,"Phéophycées","Algues brunes","gestionenligne/images_groupe/4.gif","","Les algues sont des végétaux chlorophylliens unicellulaires ou pluricellulaires constitués d'un thalle (appareil végétatif) peu différencié. Chez les algues pluricellulaires il peut avoir des formes très diverses plus ou moins ramifiées.{{n/}}Le terme de Phéophycées, ou algues brunes, est utilisé pour désigner les algues possédant en plus des pigments les plus communs (chlorophylles…) des pigments bruns.", listeGroupes.get(5)));
        listeGroupes.add(new Groupe(0,0,"Plantes à fleurs","phanérogames","gestionenligne/images_groupe/5.gif","","", listeGroupes.get(4)));
        listeGroupes.add(new Groupe(5,0,"Plantes subaquatiques","","gestionenligne/images_groupe/5.gif","","Chez les végétaux dits « supérieurs », l'appareil végétatif est un cormus, plus complexe et plus élaboré que le thalle des algues. Seront regroupés ici dans un but pratique tous les végétaux aquatiques ou subaquatiques qui ne sont pas des algues : {{g}}mousses, fougères et apparentées, et phanérogames. {{/g}}Les mousses, les fougères et espèces apparentées sont des végétaux vascularisés, mais qui se reproduisent encore au moyen de spores. On pourra les observer sur terre et en eau douce. Les phanérogames subaquatiques sont très proches des plantes à fleurs terrestres dont elles sont issues. Elles possèdent un appareil végétatif de même type. Leur tige rampante plus ou moins souterraine (le rhizome), est munie de racines et de longues feuilles souvent en ruban. Leur reproduction fait intervenir des fleurs fécondées par un pollen transporté par l'eau, et aboutit à la formation de graines, souvent protégées dans un fruit.", listeGroupes.get(11)));
        listeGroupes.add(new Groupe(6,0,"Plantes terrestres","","gestionenligne/images_groupe/6.gif","","Chez les végétaux dits « supérieurs », l'appareil végétatif est un cormus, plus complexe et plus élaboré que le thalle des algues. Seront donc regroupés ici dans un but pratique tous les végétaux aquatiques ou subaquatiques qui ne sont pas des algues : {{g}}mousses, fougères et apparentées, et phanérogames.{{n/}}{{/g}}Ce groupe sera représenté par les végétaux aériens des bords de mer ou des bords de lacs, dont les rhizomes sont plus ou moins immergés. Quantité de végétaux se développent sur l'étage supralittoral, comme certaines mousses et de nombreuses phanérogames, dont certaines présentent des adaptations au milieu salé (halophytes).", listeGroupes.get(11)));
        listeGroupes.add(new Groupe(0,0,"LICHENS","","images/pucecarre.gif","","", listeGroupes.get(1)));
        listeGroupes.add(new Groupe(7,0,"Champignons et Lichens","","gestionenligne/images_groupe/7.gif","","Les {{g}}champignons{{/g}} observables en plongée ou sur le rivage seront décrits ici. Il s'agit d'organismes hétérotrophes non photosynthétiques, comme les animaux, mais qui se reproduisent grâce à des spores. En plongée, ils sont le plus souvent visibles sous forme de moisissures discrètes. Les {{g}}lichens{{/g}} sont des organismes formés par une association très particulière (symbiose) entre les filaments d'un champignon d'une part, et des algues unicellulaires ou des cyanobactéries d'autre part. Capables de se fixer sur des substrats minéraux nus, ils ont souvent un rôle pionnier.", listeGroupes.get(14)));
        listeGroupes.add(new Groupe(0,0,"ANIMAUX","","images/pucecarre.gif","","", listeGroupes.get(1)));
        listeGroupes.add(new Groupe(8,0,"Animaux unicellulaires","","gestionenligne/images_groupe/8.gif","","Ce groupe renferme des organismes unicellulaires eucaryotes hétérotrophes fort variés, anciennement réunis sous le nom de Protozoaires. Citons les {{g}}Actinopodes{{/g}} et les {{g}}Foraminifères{{/g}}, délicats organismes planctoniques, parfois benthiques, dont les tests minéraux abondent dans les sédiments marins. En eau douce on pourra trouver des {{g}}Flagellés{{/g}}, des {{g}}Ciliés{{/g}} comme la paramécie, ainsi que des organismes parasites se reproduisant grâce à des spores ({{g}}Sporozoaires{{/g}}).Certains de ces organismes atteignent une taille macroscopique et sont visibles en plongée.", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(0,0,"EPONGES ou SPONGIAIRES","","gestionenligne/images_groupe/9.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(9,0,"Calcisponges","Éponges calcaires","gestionenligne/images_groupe/9.gif","","Ces éponges marines à spicules calcaires ont généralement une architecture simple, une petite taille, et sont observées à faible profondeur. Elles ont souvent une forme de petite outre blanchâtre ou jaunâtre (sycons) ou sont constituées d'un réseau de tubes souples entremêlés, d'aspect gélatineux (clathrines) ou arbustif (leucosoléniales).", listeGroupes.get(18)));
        listeGroupes.add(new Groupe(10,0,"Démosponges","Éponges cornéo-siliceuses","gestionenligne/images_groupe/10.gif","","Ces éponges possèdent des spicules siliceux associés à des protéines. Ces spicules sont parfois absents (éponge de toilette). Les Démosponges représentent la très grande majorité des éponges observées en plongée et possèdent des tailles, des formes, et des couleurs extrêmement variées. Elles vivent à toutes profondeurs et certaines espèces ont conquis les eaux douces.", listeGroupes.get(18)));
        listeGroupes.add(new Groupe(11,0,"Hexactinellides","Éponges de verre","gestionenligne/images_groupe/11.gif","","Ces éponges au squelette siliceux original ne seront qu'exceptionnellement observées en plongée puisqu'elles affectionnent les grandes profondeurs. Leurs spicules possèdent trois axes (Triaxonides) et donc six pointes, et ne sont pas associés à une matrice protéique. Une seule espèce peut aujourd'hui être observée en plongée scaphandre dans les eaux françaises.", listeGroupes.get(18)));
        listeGroupes.add(new Groupe(0,0,"CNIDAIRES","","gestionenligne/images_groupe/12.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(12,0,"Hydrozoaires","Hydraires, Hydroméduses...","gestionenligne/images_groupe/12.gif","","Les Hydrozoaires sont des Cnidaires le plus souvent coloniaux, fixés ou pélagiques, et leur cycle de vie est caractérisé en grande majorité par une alternance caractéristique des phases polype et méduse. Au sein de la colonie, les polypes peuvent se différencier pour accomplir différentes tâches : nutrition, reproduction, protection, excrétion... Les {{g}}Hydraires{{/g}} sont généralement de petite taille et ressemblent à des fleurs, des plumes, ou des buissons. Certains Hydrozoaires tropicaux sécrètent un exosquelette calcaire : ce sont les {{g}}Hydrocoralliaires {{/g}}(coraux de feu, coraux-dentelles...). Les {{g}}Siphonophores{{/g}} sont d'étranges colonies pélagiques et complexes longues parfois de plusieurs mètres. Ils se présentent sous la forme d'un flotteur rempli de gaz sous lequel sont agencés des méduses modifiées et des polypes très différenciés. Les {{g}}Trachylines{{/g}} sont des hydroméduses qui ont secondairement perdu la phase polype. Elles vivent le plus souvent en eaux profondes (Trachyméduses, Narcoméduses).{{g}} {{/g}}Les hydres et une hydroméduse (groupe des {{g}}Limnoméduses{{/g}}){{g}} {{/g}}ont conquis les eaux douces.", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(12,25,"Hydrozoaires benthiques","","gestionenligne/images_sousgroupe/25.gif","","Les Hydrozoaires benthiques regroupent les {{g}}Hydraires{{/g}} (les espèces dont la phase polype est dominante seront décrites ici), ainsi que les {{g}}Hydrocoralliaires{{/g}}, colonies tropicales protégées par un exosquelette calcaire.", listeGroupes.get(23)));
        listeGroupes.add(new Groupe(12,26,"Hydrozoaires pélagiques","","gestionenligne/images_sousgroupe/26.gif","","Les Hydrozoaires pélagiques regroupent les {{g}}hydroméduses au sens large{{/g}} (Limnoméduses, Narcoméduses, Trachyméduses. Les espèces d'Hydraires dont la phase méduse est dominante, comme les leptoméduses, seront également décrites ici), quelques rares {{g}}Hydraires non benthiques{{/g}} (porpites et vélelles), ainsi que les {{g}}Siphonophores{{/g}}, colonies constituées de nombreux polypes et méduses modifiés et spécialisés, longues parfois de quelques dizaines de mètres.", listeGroupes.get(23)));
        listeGroupes.add(new Groupe(13,0,"Cubozoaires","Cuboméduses","gestionenligne/images_groupe/13.gif","","Les cuboméduses (ou guêpes de mer) possèdent quatre (ou quatre groupes de) tentacules et une ombrelle cubique généralement de petite taille (quelques centimètres). Elles étaient autrefois classées au sein des Scyphozoaires mais leur structure et leur biologie en font désormais un groupe à part. Ces méduses, extrêmement dangereuses, sont essentiellement tropicales. On ne dénombre aujourd'hui qu'une seule espèce métropolitaine.", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(14,0,"Scyphozoaires","Méduses vraies","gestionenligne/images_groupe/14.gif","","Les {{g}}Scyphozoaires{{/g}} rassemblent les méduses dites « vraies », généralement de grande taille avec de longs tentacules ({{s}}Séméostomes{{/s}}). Ce sont les méduses le plus souvent rencontrées en plongée. Certaines scyphoméduses n'ont pas de tentacules mais un réseau de tubes filtrants ({{s}}Rhizostomes{{/s}}). D'autres, qui vivent généralement en eaux profondes, présentent une ombrelle divisée en deux par un sillon transversal ({{s}}Coronates{{/s}}). Les {{g}}Staurozoaires{{/g}} sont des méduses qui ont abandonné le mode de vie pélagique et qui se sont fixées secondairement. Elles ont alors la forme d'un petit entonnoir (lucernaires).", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(14,48,"Scyphozoaires","méduses vraies","gestionenligne/images_sousgroupe/48.gif","","Les {{g}}Scyphozoaires{{/g}} rassemblent les méduses dites « vraies » (scyphoméduses), celles que l'on va principalement observer à la plage ou en plongée prés de la surface. Elles atteignent parfois de grandes tailles, et sont caractérisées par l'absence de vélum sous l'ombrelle (méduses {{D:acraspèdes}}acraspèdes{{/D}}).", listeGroupes.get(27)));
        listeGroupes.add(new Groupe(14,47,"Staurozoaires","lucernaires","gestionenligne/images_sousgroupe/47.gif","","Les {{g}}Staurozoaires{{/g}} ou{{g}} stauroméduses {{/g}}sont des méduses qui vivent fixées par un pédoncule sur des roches ou des végétaux à la phase adulte, bien qu'elles puissent se déplacer en nageant sporadiquement. Elles sont représentées par les lucernaires.", listeGroupes.get(27)));
        listeGroupes.add(new Groupe(15,0,"Alcyonides","Coraux mous","gestionenligne/images_groupe/15.gif","","Les alcyons forment des colonies encroûtantes ou dressées qui peuvent réguler la quantité d'eau contenue dans leurs tissus, ce qui leur permet de s'ériger ou au contraire de se ratatiner. Pour cette raison les alcyons sont aussi appelés coraux mous. Ils possèdent des polypes à huit tentacules munis de fines ramifications : les pinnules.", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(16,0,"Gorgonaires","Gorgones et Corallides","gestionenligne/images_groupe/16.gif","","Les gorgones ou « éventails de mer » sont bien connues des plongeurs. Ces colonies très ramifiées peuvent atteindre de grandes tailles. Leur squelette est à la fois souple et résistant. En général, les colonies sont disposées sur un même plan perpendiculaire au courant, pour faciliter la collecte des particules alimentaires. Les Gorgonaires renferment également l'ancien ordre des Corallides, ou coraux vrais, dont l'endosquelette compact est enrichi en sclérites. C'est à ce groupe qu'appartiennent de nombreuses espèces tropicales et récifales aux formes et couleurs multiples. Les Gorgonaires possèdent des polypes à huit tentacules munis de fines ramifications : les pinnules.", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(17,0,"Pennatulaires","Plumes de mer","gestionenligne/images_groupe/17.gif","","Les Pennatulaires sont des Cnidaires coloniaux qui vivent ancrés dans le sédiment par un pédoncule. Celui-ci est surmonté d'une masse axiale charnue, sur laquelle s'implantent latéralement de nombreuses lames foliacées porteuses des polypes. Ils sont représentés par les vérétilles et les pennatules (plumes de mer). Ces colonies, d'ordinaire visibles la nuit, sont capables de mouvements de torsion et peuvent s'enfouir dans le sédiment. Leurs polypes possèdent huit tentacules munis de fines ramifications : les pinnules.", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(18,0,"Autres Octocoralliaires","Stolonifères...","gestionenligne/images_groupe/18.gif","","Il existe trois autres groupes mineurs d'Octocoralliaires. Les {{g}}Stolonifères{{/g}} sont des octocoralliaires coloniaux dont les polypes sont reliés par un stolon corné qui adhère au substrat. Ce stolon érige des gaines tubulaires à l'intérieur desquelles les polypes peuvent se rétracter. Les {{g}}Hélioporides{{/g}} et les {{g}}Télestacés{{/g}} ne sont représentés que par quelques espèces tropicales, comme le corail bleu. Tous ces organismes possèdent des polypes à huit tentacules munis de fines ramifications : les pinnules.", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(19,0,"Anémones de mer au sens large, cérianthes","","gestionenligne/images_groupe/19.gif","","Nous avons choisi de regrouper ici 4 groupes de Cnidaires Hexacoralliaires d'apparence semblable, toujours dépourvus de squelette calcaire. Les {{g}}Actiniaires{{/g}} sont toujours solitaires et collés au substrat par un disque adhésif (anémones de mer au sens strict). Les {{g}}Cérianthaires{{/g}} sont solitaires et vivent dans un tube d'où rayonnent deux couronnes de tentacules souvent vivement et différemment colorés (cérianthes). Les {{g}}Zoanthides{{/g}} présentent une double couronne de tentacules, sont coloniaux, reliés entre eux, et colonisent très souvent de grandes surfaces (anémones encroûtantes). Les {{g}}Corallimorphaires{{/g}} enfin sont comme les Zoanthides des organismes pouvant coloniser de grandes surfaces. Les tentacules de leurs polypes, nombreux et disposés en plusieurs cercles, sont souvent terminés en boutons (Corynactis{{/i}}). Les tentacules des polypes de tous ces organismes sont lisses et leur nombre est multiple de six.", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(19,6,"Anémones","","gestionenligne/images_sousgroupe/6.gif","","Les {{s}}anémones de mer{{/s}} au sens strict (ou {{g}}Actiniaires{{/g}}) sont des polypes solitaires souvent colorés, en général collés à un substrat dur par un large disque pédieux, et toujours dépourvus de squelette. Les tentacules, disposés en cycles alternés et parfois très nombreux, sont lisses et leur extrémité est pointue ou arrondie (jamais d'{{D:acrosphères}}acrosphères{{/D}}).", listeGroupes.get(34)));
        listeGroupes.add(new Groupe(19,7,"Cérianthes","","gestionenligne/images_sousgroupe/7.gif","","Les {{s}}cérianthes{{/s}} (ou {{g}}Cérianthaires{{/g}}) sont de grands polypes solitaires fouisseurs. Au sommet de la colonne, deux verticilles de tentacules de tailles différentes, les plus grands à l'extérieur. Ces polypes sont rétractables dans un fourreau constitué de cnidocytes éclatés et partiellement enfoui. Les cérianthes ont bien souvent des tentacules de couleur vive, parfois bariolés. Les deux verticilles ont la plupart du temps une couleur différente.", listeGroupes.get(34)));
        listeGroupes.add(new Groupe(19,5,"Anémones encroûtantes","","gestionenligne/images_sousgroupe/5.gif","","Les {{s}}anémones encroûtantes{{/s}} (ou {{g}}Zoanthides{{/g}}) ressemblent aux Actiniaires, aux différences près qu'elles ne possèdent toujours qu'un double cycle de tentacules, et que les polypes, toujours de petite taille, sont reliés entre eux par un coenenchyme parfois minéralisé. Les colonies de Zoanthides recouvrent bien souvent de vastes surfaces.", listeGroupes.get(34)));
        listeGroupes.add(new Groupe(19,8,"Corallimorphaires","","gestionenligne/images_sousgroupe/8.gif","","Les {{g}}Corallimorphaires{{/g}} sont des Hexacoralliaires solitaires ressemblant aux Madréporaires (d'où leur nom), mais toujours dépourvus d'exosquelette calcaire. Les polypes ont souvent une forme d'assiette avec de petits tentacules disposés en plusieurs cycles et abritant le plus souvent des zooxanthelles. L'extrémité des tentacules des Corallimorphaires porte une petite sphère, l'{{D:acrosphère}}acrosphère{{/D}}. Ces Cnidaires sont discrets en métropole, mais très répandus en zone tropicale, où ils atteignent parfois de grandes tailles.", listeGroupes.get(34)));
        listeGroupes.add(new Groupe(20,0,"Scléractiniaires","ou Madréporaires","gestionenligne/images_groupe/20.gif","","Ces Anthozoaires sont caractérisés par un squelette calcaire à l'intérieur duquel les polypes peuvent se recroqueviller. Chez les espèces solitaires ce squelette a fréquemment la forme d'une molaire, d'où leurs autres noms de « dents de cochon » ou « dents de chien ». Les Scléractiniaires, ou Madréporaires, sont assez discrets dans les eaux tempérées, mais très répandus au sein des récifs coralliens. Ils sont solitaires ou coloniaux, fixés ou non, et leurs polypes ont un nombre de tentacules lisses multiple de six.", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(21,0,"Antipathaires","Coraux noirs","gestionenligne/images_groupe/21.gif","","Ces Anthozoaires ont l'apparence de branches d'arbre ramifiées, parfois spiralées, et peuvent être confondus avec des Gorgonaires, si ce n'est que les tentacules de leurs polypes sont toujours lisses (pas de pinnules) et qu'ils ne sont jamais rétractables. Leur exosquelette épineux, autrefois utilisé en joaillerie, est de couleur sombre d'où leur autre nom : les coraux noirs. Leurs polypes sont très modifiés : la règle du multiple de six pour les tentacules n'est plus vérifiée et la bouche n'est presque plus visible.", listeGroupes.get(22)));
        listeGroupes.add(new Groupe(0,0,"CTENAIRES","","gestionenligne/images_groupe/22.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(22,0,"Cténophores","ou Cténaires","gestionenligne/images_groupe/22.gif","","Ces organismes pélagiques (rarement benthiques) sont souvent confondus avec les méduses. Les Cténophores ou Cténaires sont exclusivement marins, et aisément identifiables à leurs huit rangées de palettes ciliées locomotrices qui s'irisent à la lumière. Ces étranges organismes globuleux, délicats et très fragiles, ont un aspect variable : en forme de bille, de poire, de ballon de rugby, de cone, et même aplatie et rubanée chez les ceintures de Vénus. Les Cténaires possèdent souvent des tentacules armés de colloblastes (cellules adhésives) pour la capture du plancton, et sont doués pour la plupart d'une plus ou moins forte bioluminescence.", listeGroupes.get(41)));
        listeGroupes.add(new Groupe(0,0,"VERS","","gestionenligne/images_groupe/23.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(23,0,"Plathelminthes","Vers plats","gestionenligne/images_groupe/23.gif","","Les Plathelminthes sont comme leur nom l'indique des vers plats et extrêmement fins. C'est à ce groupe qu'appartiennent de nombreuses espèces parasites, comme les douves et les ténias. En milieu marin on n'observera que des formes libres souvent vivement colorées : les planaires (au sens large). Une face ventrale tapissée de nombreux cils leur permet de glisser sur n'importe quelle surface. Lorsqu'ils nagent, ces vers ondulent de manière caractéristique, en faisant tourbillonner l'eau environnante. En eau douce, ces vers sont plus discrets, plus ternes, cachés sous les pierres ou rampant sur la vase. On dénombre aussi quelques espèces terrestres.", listeGroupes.get(43)));
        listeGroupes.add(new Groupe(24,0,"Polychètes errantes","","gestionenligne/images_groupe/24.gif","","Ces Annélides sont exclusivement marines, et leur segmentation est dite homonome : les segments du ver sont pratiquement identiques sur toute sa longueur. Ce sont des formes libres qui se déplacent rapidement, souvent prédatrices et carnassières. Chaque segment, hormis la tête et la queue, porte des expansions foliacées, les parapodes, aux formes variées et servant à la locomotion. (La scission Errantes-Sédentaires chez les Annélides Polychètes est aujourd'hui abandonnée, la systématique de ce vaste groupe n'étant pas si simple. Nous la conservons néanmoins ici dans un but pratique).", listeGroupes.get(43)));
        listeGroupes.add(new Groupe(25,0,"Polychètes sédentaires","","gestionenligne/images_groupe/25.gif","","Ces Annélides sont exclusivement marines, et leur segmentation est dite hétéronome : elle varie d'une extrémité à l'autre du ver, variation dûe au mode de vie fouisseur ou tubicole de ces organismes. Certaines vivent dans le sédiment dans des galeries en J, en U ou en Y, d'autres réalisent un tube plus ou moins grand et plus ou moins solide, constitué de mucus et de débris divers, duquel n'émerge qu'un panache de branchies ou des tentacules, souvent vivement colorés, et qui servent à la respiration et à la nutrition. (La scission Errantes-Sédentaires chez les Annélides Polychètes est aujourd'hui abandonnée, la systématique de ce vaste groupe n'étant pas si simple. Nous la conservons néanmoins ici dans un but pratique).", listeGroupes.get(43)));
        listeGroupes.add(new Groupe(26,0,"Oligochètes et Hirudinées","Clitellates","gestionenligne/images_groupe/26.gif","","Les {{g}}Oligochètes{{/g}} (groupe des vers de terre et des tubifex) ne seront en principe qu'exceptionnellement rencontrés en plongée. Ils pourront être observés lors de prélèvements de sédiments par exemple. Ce sont des vers lisses qui ne présentent plus les {{D:parapodes}}parapodes{{/D}} des Polychètes. Ils sont marins, dulcicoles, et terrestres. Les {{g}}Hirudinées{{/g}} (ou Achètes) sont représentées par les sangsues. Ces vers ectoparasites pourront être observés principalement en eaux douces, mais aussi en mer fixés sur des raies et certains poissons plats.", listeGroupes.get(43)));
        listeGroupes.add(new Groupe(27,0,"Autres « vers » subaquatiques","","gestionenligne/images_groupe/27.gif","","Nous avons choisi de regrouper ici quelques groupes zoologiquement différents mais qui ont tous en commun un aspect vermiforme.Les {{g}}Némertes{{/g}} sont des vers aplatis, rubanés, souvent vivement colorés, très fragiles et fortement extensibles : certaines espèces atteignent plusieurs dizaines de mètres ! Les {{g}}Echiuriens{{/g}} ou vers cuiller, telle la bonellie, vivent dans le sédiment ou les anfractuosités de la roche. Ils possèdent une trompe bifide. Les {{g}}Priapuliens{{/g}}, {{g}}Sipunculiens{{/g}} et {{g}}Entéropneustes{{/g}}, rarement rencontrés, sont d'étranges vers fouisseurs qui se nourrissent en ingérant et en filtrant le sable.Tous ces vers sont exclusivement marins, seuls quelques rares Némertes étant dulcicoles et terrestres.", listeGroupes.get(43)));
        listeGroupes.add(new Groupe(0,0,"MOLLUSQUES","","gestionenligne/images_groupe/28.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(28,0,"Bivalves","ou Lamellibranches","gestionenligne/images_groupe/28.gif","","Les Mollusques Bivalves (ou Lamellibranches) possèdent toujours une coquille calcaire constituée de deux valves. Certaines formes sont fixées par une valve au substrat et d'autres, plus nombreuses, sont libres, cachées dans les anfractuosités de la roche ou enfouies dans les substrats meubles en ne laissant apparaître que l'extrémité de leurs siphons qui servent à la nutrition et à la respiration. Certaines espèces sont perforantes. Les bivalves atteignent parfois de grandes tailles, tels les nacres ou les bénitiers. On peut aussi trouver des Bivalves en eau douce.", listeGroupes.get(49)));
        listeGroupes.add(new Groupe(29,0,"Céphalopodes","","gestionenligne/images_groupe/29.gif","","Les Céphalopodes sont des Mollusques très spécialisés. Leur pied est transformé en tentacules recouverts de ventouses. A la radula s'ajoute un puissant bec corné. Certaines espèces possèdent encore une coquille externe (Nautiloïdes), d'autres un reliquat de coquille interne (calmars, seiches), d'autres enfin en sont totalement dépourvues (pieuvres). Leur manteau contient de très nombreuses cellules pigmentaires (les chromatophores) qui leur permettent de changer de coloration à volonté. Ce sont les plus rapides de tous les invertébrés aquatiques. Le plus souvent, ils déroutent leurs prédateurs (poissons, cétacés...) en lâchant un nuage d'encre noire.", listeGroupes.get(49)));
        listeGroupes.add(new Groupe(30,0,"Gastéropodes Pulmonés","","gestionenligne/images_groupe/30.gif","","Ces Mollusques ne seront en principe observés qu'en eau douce, plus rarement en milieu marin. Ils ressemblent fortement aux Gastéropodes Prosobranches, mais leurs branchies ont disparu, la cavité palléale faisant office de poumon, et leur coquille, quand elle existe, est beaucoup plus fine. Ce groupe est représenté par des organismes herbivores capables de s'aventurer hors de l'eau (planorbes et limnées en eau douce, limaces celtiques sur l'estran, etc…).", listeGroupes.get(49)));
        listeGroupes.add(new Groupe(31,0,"Gastéropodes Prosobranches","","gestionenligne/images_groupe/31.gif","","Les Gastéropodes Prosobranches sont des Mollusques possédant une coquille unique, épaisse et solide. Leurs branchies sont ramenées à l'avant du cœur. La coquille a une forme extrêmement variable, depuis le simple « chapeau chinois » des patelles jusqu'aux torsades des vermets. Ils sont presque exclusivement marins ; certaines espèces ont conquis les eaux douces.", listeGroupes.get(49)));
        listeGroupes.add(new Groupe(31,50,"Vermets","Vermétidés","gestionenligne/images_sousgroupe/50.gif","","Les Vermétidés sont des gastéropodes prosobranches à la coquille tubuliforme et aux spires disjointes et irrégulières. L'ouverture est ronde, l'opercule petit et rudimentaire. La coquille est fixée au substrat.", listeGroupes.get(53)));
        listeGroupes.add(new Groupe(32,0,"Gastéropodes Opisthobranches","","gestionenligne/images_groupe/32.gif","","Les Gastéropodes Opisthobranches sont caractérisés par des branchies situées en arrière du coeur. Ils sont essentiellement représentés par les « limaces de mer » au sens large, c'est-à-dire les élysies, les lièvres de mer et surtout le vaste ensemble des Nudibranches aux formes et couleurs extrêmement variées. De nombreuses espèces possèdent encore une coquille réduite.", listeGroupes.get(49)));
        listeGroupes.add(new Groupe(32,34,"Nudibranches Doridiens","","gestionenligne/images_sousgroupe/34.gif","","Les {{g}}Nudibranches{{/g}}, comme leur nom l'indique, ont des branchies nues (non protégées). Les {{g}}Doridiens{{/g}} ont en général un corps aplati. L'anus est dorsal et est entouré complètement ou partiellement par des branchies ramifiées qui peuvent être rétractées voire absentes. Habituellement, leur manteau est armé de spicules calcaires internes. Ce sont des mangeurs de Spongiaires, de Bryozoaires, ou d'Ascidies. Les tailles, les couleurs et motifs sont extrêmement variables.", listeGroupes.get(55)));
        listeGroupes.add(new Groupe(32,35,"Nudibranches Eolidiens","","gestionenligne/images_sousgroupe/35.gif","","Les {{g}}Nudibranches{{/g}}, comme leur nom l'indique, ont des branchies nues (non protégées). Les {{g}}Eolidiens{{/g}} possèdent un corps long et effilé portant des {{D:cérates}}cérates{{/D}} simples alignés sur plusieurs rangées ou en bouquets. La tête porte une paire de tentacules sans gaine et une paire de rhinophores. Les coins antérieurs du pied sont parfois effilés en tentacules. Ce sont pour l'essentiel des mangeurs de Cnidaires. Les tailles, les couleurs et motifs sont extrêmement variables.", listeGroupes.get(55)));
        listeGroupes.add(new Groupe(32,36,"Autres Nudibranches","","gestionenligne/images_sousgroupe/36.gif","","Les {{g}}Nudibranches{{/g}}, comme leur nom l'indique, ont des branchies nues (non protégées). Sont regroupés ici les Nudibranches qui ne sont ni des Doridiens, ni des Eolidiens. Ce sont tous des mangeurs de Cnidaires : les {{g}}D{{/g}}{{g}}endronotacés{{/g}} possèdent des appendices dorsaux paires en aiguilles, en cigares ou branchus, et une tête en général pourvue de rhinophores, en tube ou en coupe, rétractiles dans des fourreaux. Ils sont caractérisés par un vélum oral ({{i}}Dendronotus{{/i}}, {{i}}Phylliroe{{/i}}, {{i}}Tethys{{/i}}, {{i}}Tritonia{{/i}}...). Les {{g}}Arminacés{{/g}} (ou Arminiens) possèdent un vélum sur la tête qui constitue le seul caractère commun évident. Les appendices dorsaux sont présents ou non. La tête posséde en général seulement des rhinophores contractiles qui peuvent s'enrouler sans gaine. Enfin, certaines familles, comme les {{g}}Janolidés{{/g}} et les {{g}}Dotidés{{/g}}, ont encore une place incertaine au sein des 4 groupes de Nudibranches. Elles seront décrites ici.", listeGroupes.get(55)));
        listeGroupes.add(new Groupe(32,37,"Autres Opisthobranches","","gestionenligne/images_sousgroupe/37.gif","","Sont regroupés ici tous les Opisthobranches non Nudibranches. Tous les groupes suivants possèdent en totalité ou en partie des espèces munies de coquilles. Les {{g}}Aplysiomorphes{{/g}} (ou Anaspides) ont pour la plupart une grande taille et sont herbivores (lièvres de mer). Les {{g}}Sacoglosses{{/g}} possèdent des parapodies dorsales, des replis du manteau (élysies, thuridilles...). Les {{g}}Pleurobranchomorphes{{/g}} (ou Notaspides) possèdent une cavité palléale latérale (à droite) et une branchie pourvue d'une double rangée de lamelles (berthelles, pleurobranches, ombrelles...). Les {{g}}Bullomorphes{{/g}} (ou Céphalaspides) possèdent une tête élargie en bouclier, dépourvue de rhinophores, mais pourvue d'yeux développés (bulles, actéons, philines...). Les {{g}}Ptéropodes{{/g}} (Gymnosomes et Thécosomes) sont d'étranges limaces pélagiques se déplaçant dans le plancton au moyen d'expansions foliacées du manteau ({{i}}Clio{{/i}}, {{i}}Cavolinia{{/i}}, {{i}}Cymbulia{{/i}}...).", listeGroupes.get(55)));
        listeGroupes.add(new Groupe(33,0,"Autres Mollusques","chitons, dentales...","gestionenligne/images_groupe/33.gif","","Il existe d'autres petits groupes de Mollusques, moins connus des plongeurs. Les seuls qui pourront être aperçus le plus souvent sont les {{g}}Polyplacophores{{/g}}, également appelés chitons. Ces organismes sont le plus facilement observés à marée basse, fixés aux rochers. Leurs 8 plaques dorsales les font ressembler à des cloportes. Les {{g}}Scaphopodes{{/g}} ou dentales affectionnent en général les grandes profondeurs. Leur coquille caractéristique en forme de défense d'éléphant peut être retrouvée dans les laisses de mer. Les {{g}}Aplacophores{{/g}} enfin sont de petits Mollusques vermiformes très discrets, dont le manteau secrète une cuticule contenant des spicules calcaires.", listeGroupes.get(49)));
        listeGroupes.add(new Groupe(0,0,"LOPHOPHORATES","","gestionenligne/images_groupe/34.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(34,0,"Bryozoaires, Brachiopodes et Phoronidiens","","gestionenligne/images_groupe/34.gif","","Les représentants de ces trois embranchements ({{i}}anciennement réunis sous le nom de Lophophorates{{/i}}) regroupent des organismes d'aspects très différents, mais qui ont en commun un polype particulier appelé zoïde, équipé d'une couronne de tentacules en forme de fer à cheval ou de spirale : le lophophore. Les {{g}}Bryozoaires{{/g}} (ou Ectoproctes) sont des organismes coloniaux. Leur squelette calcaire est souvent très délicat (dentelles marines) et a un aspect variable : il est parfois encroûtant, parfois arbustif, et est constitué de très nombreuses logettes minuscules contenant chacune un zoïde. Comme chez les Hydrozoaires, on observe souvent une différenciation des zoïdes qui peuvent avoir des rôles variés au sein de la colonie. Certaines espèces sont parfois trouvées en eau douce.{{n/}}On dénombre encore deux autres petits embranchements qui, comparés aux Bryozoaires, ne comptent que peu d'espèces : il s'agit des {{g}}Brachiopodes{{/g}} et des {{g}}Phoronidiens{{/g}}.", listeGroupes.get(61)));
        listeGroupes.add(new Groupe(34,38,"Bryozoaires arbustifs","","gestionenligne/images_sousgroupe/38.gif","","Sont regroupées ici les espèces de Bryozoaires dont la colonie présente un port ramifié, souvent arborescent.{{n/}}[NB : ce regroupement est purement pratique et ne représente pas forcément un taxon homogène.]", listeGroupes.get(62)));
        listeGroupes.add(new Groupe(34,39,"Bryozoaires encroûtants","","gestionenligne/images_sousgroupe/39.gif","","Sont regroupées ici les espèces de Bryozoaires dont la colonie présente un port encroûtant.{{n/}}[NB : ce regroupement est purement pratique et ne représente pas forcément un taxon homogène.]", listeGroupes.get(62)));
        listeGroupes.add(new Groupe(34,41,"Brachiopodes","","gestionenligne/images_sousgroupe/41.gif","","Les {{g}}Brachiopodes{{/g}}, jadis abondants, sont aujourd'hui rares et ne seront rencontrés que par les plongeurs chanceux. Ils possèdent deux valves et peuvent être confondus avec des Mollusques Lamellibranches, mais leur physiologie et leur anatomie (présence du lophophore notamment) sont totalement différentes.", listeGroupes.get(62)));
        listeGroupes.add(new Groupe(34,40,"Phoronidiens","","gestionenligne/images_sousgroupe/40.gif","","Les {{g}}Phoronidiens{{/g}} sont les Lophophorates qui atteignent les plus grandes tailles. Ils forment des colonies d'individus bien visibles non reliés entre eux et pouvant recouvrir de grandes surfaces. Ils ne possèdent ni exosquelette ni valves. Les zoïdes sont nus, souvent logés dans un tube membraneux.", listeGroupes.get(62)));
        listeGroupes.add(new Groupe(0,0,"ARTHROPODES","","gestionenligne/images_groupe/35.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(35,0,"Crustacés Malacostracés","crabes, crevettes...","gestionenligne/images_groupe/35.gif","","Les Malacostracés, ou « Crustacés supérieurs » (ancienne appellation), regroupent les Crustacés les plus communément observés par les plongeurs, depuis les petits gammares jusqu'aux crabes et araignées de mer, en passant par les crevettes, les langoustes, les bernard-l'ermite… Leur corps est généralement composé de huit segments thoraciques et de six segments abdominaux. Certaines espèces, comme par exemple les écrevisses, ont conquis les eaux douces.", listeGroupes.get(67)));
        listeGroupes.add(new Groupe(35,79,"Isopodes, Amphipodes, Mysidacés...","Péracarides","gestionenligne/images_sousgroupe/79.gif","","Nous regroupons ici l'ensemble des {{g}}Péracarides{{/g}}, c'est-à-dire essentiellement les {{s}}Isopodes{{/s}} (anilocres, idotées, aselles, ligies...), les {{s}}Amphipodes{{/s}} (talitres, gammares, caprelles...), et les {{s}}Mysidacés{{/s}} (Mysis...)", listeGroupes.get(68)));
        listeGroupes.add(new Groupe(35,81,"Squilles","Stomatopodes","gestionenligne/images_sousgroupe/81.JPG","","Le groupe des {{g}}Stomatopodes{{/g}} est celui des squilles, des Crustacés allongés dont la 2ème paire de pattes, la plus développée, évoque les pattes ravisseuses des mantes religieuses.", listeGroupes.get(68)));
        listeGroupes.add(new Groupe(35,75,"Crevettes et apparentés : Caridés, Sténopodidés...","","gestionenligne/images_sousgroupe/75.gif","","Le terme crevette est vague et désigne des Crustacés appartenant à des groupes bien distincts et parfois de morphologie très différente : les {{g}}{{i}}Caridea{{/i}}{{/g}} (rostre proéminent entre les yeux, carapace lisse, telson en éventail) regroupent la grande majorité des espèces rencontrées en plongée. Elles sont benthiques, fouisseuses, et même pélagiques. Les {{g}}{{i}}Stenopodidea{{/i}}{{/g}} possèdent une carapace épineuse et ont leur troisième paire de chélipèdes bien développée (crevettes nettoyeuses). Les {{i}}{{g}}Thalassinidea{{/g}}{{/i}} sont représentés par des crevettes fouisseuses caractérisées par un céphalothorax bien développé, une première paire de péréiopodes transformée en grosses pinces asymétriques et par un abdomen allongé et aplati (callianasses, upogébies…). Les {{i}}{{g}}Peneidea{{/g}}{{/i}} forment un groupe de grandes crevettes au rostre denté proéminent et de grand intérêt commercial (gambas…). Enfin, dans un but pratique, nous avons choisi d'inclure ici, à cause de leur ressemblance avec les crevettes, le groupe des {{g}}{{i}}Euphausiacés{{/i}}{{/g}}, représenté par des espèces planctoniques appelées krill, proches des Malacostracés Décapodes.", listeGroupes.get(68)));
        listeGroupes.add(new Groupe(35,76,"Homards, langoustes...","Palinoures et Astacoures","gestionenligne/images_sousgroupe/76.gif","","Nous regroupons ici les {{g}}Palinoures{{/g}} et les {{g}}Astacoures{{/g}}, représentés par les espèces de Crustacés marcheurs les plus communs : langoustes, cigales, écrevisses, homards, langoustines... Ils sont néanmoins capables de déplacements rapides en marche arrière et au dessus du sol en repliant promptement leur abdomen, allongé et bien développé.", listeGroupes.get(68)));
        listeGroupes.add(new Groupe(35,77,"Bernard l'ermite, galathées...","Anomoures","gestionenligne/images_sousgroupe/77.gif","","Les {{g}}Anomoures{{/g}} sont des Crustacés Décapodes marcheurs. Ils sont caractérisés par une première paire de pattes qui portent toujours des pinces le plus souvent dissymétriques, et par un abdomen réduit. Les Bernard-l'ermite (ou pagures) protègent cet abdomen dans une coquille vide. Les galathées, les crabes-porcelaine, se distinguent des autres Crustacés par leurs 3 paires de pattes marcheuses.", listeGroupes.get(68)));
        listeGroupes.add(new Groupe(35,78,"Crabes, araignées de mer...","Brachyoures","gestionenligne/images_sousgroupe/78.gif","","Les {{g}}Brachyoures{{/g}} sont des Crustacés Décapodes marcheurs. L'abdomen est complètement replié sous le céphalothorax. Ils sont représentés par les crabes et les araignées de mer.", listeGroupes.get(68)));
        listeGroupes.add(new Groupe(36,0,"Crustacés Cirripèdes","balanes, anatifes...","gestionenligne/images_groupe/36.gif","","Les Crustacés Cirripèdes peuvent être confondus à première vue avec des Mollusques puisqu'ils possèdent une carapace calcaire souvent fixée au substrat. Le plus souvent, cette carapace est d'aspect conique, ne laissant sortir qu'une palette de cirres (d'où le nom du groupe) servant à la capture des particules planctoniques. Ce sont des organismes extrêmement abondants, fréquemment observés à marée basse sur les rochers, sur la coque des bateaux, et même sur d'autres animaux (balanes). D'autres formes possèdent un pédoncule (anatifes, pouce-pieds…). D'autres enfin ont un aspect totalement modifié par leur mode de vie parasitaire : il s'agit principalement des sacculines qui envahissent l'abdomen des crabes et provoquent leur castration.", listeGroupes.get(67)));
        listeGroupes.add(new Groupe(37,0,"Autres Crustacés","","gestionenligne/images_groupe/37.gif","","Ces autres groupes de Crustacés dits « inférieurs » (ancienne appellation) sont excessivement abondants en milieu marin puisqu'ils constituent (avec les larves) l'essentiel du zooplancton. Ils ne seront pas observés directement par le plongeur, le plus souvent à cause de leur très petite taille, mais ils le seront aisément au moyen d'une simple loupe binoculaire, après une collecte au filet à plancton. Certains {{g}}Copépodes{{/g}} parasites sont toutefois visibles en plongée. Ces organismes se présentent sous des formes extrêmement variables. Les {{g}}Ostracodes{{/g}} présentent en outre la particularité d'être enfermés dans une carapace bivalve. Certains de ces petits Crustacés peuvent être observés en eau douce, comme les cyclops et certains {{g}}Branchiopodes{{/g}} (daphnies, artémias, triops...) Il existe encore d'autres groupes de Crustacés, difficilement observables en plongée.", listeGroupes.get(67)));
        listeGroupes.add(new Groupe(38,0,"Chélicérates aquatiques","","gestionenligne/images_groupe/38.gif","","Les Chélicérates sont, contrairement aux Crustacés, Insectes, et Myriapodes, des Arthropodes munis de pièces buccales particulières pour déchiqueter leurs proies : les chélicères. Les {{g}}Arachnides{{/g}} sont essentiellement terrestres et sont représentés par les araignées, les scorpions et les acariens. Il existe deux petits groupes de Chélicérates marins : les {{g}}Mérostomes{{/g}}, ou Xyphosures, sont représentés par les limules, d'étranges organismes qui possèdent une carapace dorsale en forme de fer à cheval. Jadis abondants, ces animaux forment aujourd'hui un groupe mineur. Les {{g}}Pycnogonides{{/g}}, ou Pantopodes, sont des organismes bizarres qui possèdent huit pattes grêles et qui ressemblent à des araignées. Ces deux groupes seront en principe rarement observés en plongée. En eau douce on pourra rencontrer quelques acariens et quelques rares araignées, comme l'argyronète.", listeGroupes.get(67)));
        listeGroupes.add(new Groupe(39,0,"Hexapodes aquatiques","Insectes...","gestionenligne/images_groupe/39.gif","","Les {{g}}Hexapodes{{/g}} seront observés quasi-exclusivement en eaux douces. Il s'agit, comme leur nom l'indique, d'Arthropodes munis de trois paires de pattes. Certaines espèces, appartennant aux groupes primitifs des {{s}}Collemboles{{/s}} et des {{s}}Archéognathes{{/s}}, sont inféodées aux flaques et aux rochers de l'étage supralittoral. D'autres, bien plus nombreuses, possèdent en général deux paires d'ailes à l'état adulte. Il s'agit des {{s}}Insectes{{/s}}. Un grand nombre d'Insectes passent toute leur vie dans l'eau ou sur l'eau (dytiques, nèpes, ranâtres…) mais d'autres vivent dans l'eau uniquement à l'état larvaire avant de se transformer en adulte et de quitter le milieu aquatique (libellules, éphémères, moustiques, etc…).", listeGroupes.get(67)));
        listeGroupes.add(new Groupe(0,0,"ECHINODERMES","","gestionenligne/images_groupe/40.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(40,0,"Astérides","étoiles de mer","gestionenligne/images_groupe/40.gif","","Les Astérides sont représentés par les {{g}}étoiles de mer{{/g}}. Ces organismes exclusivement marins ont des formes, des couleurs et des tailles fort variables. Ils possèdent en général 5 bras, mais certaines espèces peuvent en avoir davantage. Il arrive à l'inverse que ces bras soient absents : l'étoile a alors la forme d'un pentagone. En phase de régénération ou suite à un développement anormal, une espèce possédant normalement 5 bras peut présenter un nombre de bras inférieur ou supérieur à 5. Les étoiles de mer sont des prédateurs avides, le plus souvent de mollusques bivalves ou d'autres Echinodermes.", listeGroupes.get(79)));
        listeGroupes.add(new Groupe(41,0,"Échinides","oursins","gestionenligne/images_groupe/41.gif","","Les Echinides regroupent l'ensemble des {{g}}oursins{{/g}}. Ils sont exclusivement marins. De forme sphérique en général (oursins réguliers), il arrive que ces organismes adoptent une forme de disque, de cœur…(oursins irréguliers). Certaines espèces arpentent la roche ou le coralligène en quête de nourriture (algues essentiellement), d'autres sont enfouies dans les substrats meubles. Les piquants ont des formes et des tailles fort variables, très fins et très longs chez les oursins diadème par exemple, minuscules voire inexistants chez certaines espèces fouisseuses, comme les dollars des sables.", listeGroupes.get(79)));
        listeGroupes.add(new Groupe(41,45,"Oursins réguliers","","gestionenligne/images_sousgroupe/45.gif","","Ce sont le plus souvent des oursins trouvés à la surface et sur substrat dur (roche). Le test est circulaire et la bouche (ventrale) comme l'anus (opposé et aboral) sont centrés. Les zones ambulacraires et interambulacraires s'intercalent de façon régulière.", listeGroupes.get(81)));
        listeGroupes.add(new Groupe(41,46,"Oursins irréguliers","","gestionenligne/images_sousgroupe/46.gif","","Ce sont les oursins de sable dont la vie se passe principalement dans les sédiments (sables ou vases). Leur contour est allongé. La bouche a migré vers l'avant, l'anus vers l'arrière.", listeGroupes.get(81)));
        listeGroupes.add(new Groupe(42,0,"Crinoïdes","comatules","gestionenligne/images_groupe/42.gif","","Les Crinoïdes sont des Echinodermes qui ont la particularité de vivre fixés. Bouche et anus sont situés sur la même face, au centre des bras. Le nombre de ces bras, qui sont grêles et munis d'expansions, est variable (en général 10, davantage chez les formes tropicales). Ils regroupent les {{g}}comatules{{/g}}, communes à faible profondeur, et les {{g}}lys de mer{{/g}}, plus profonds. Dérangées, les comatules peuvent se détacher de leur support pour aller se fixer plus loin, à l'aide de crampons articulés. Ils sont en outre capables de nager grâce à des mouvements coordonnés de leurs bras !", listeGroupes.get(79)));
        listeGroupes.add(new Groupe(43,0,"Ophiurides","ophiures","gestionenligne/images_groupe/43.gif","","Les {{g}}ophiures{{/g}} rappellent les étoiles de mer à la différence près que leurs bras, toujours au nombre de cinq à la base, sont grêles et fragiles. Ces organismes sont souvent observés sous les pierres et dans ou à la surface de certains fonds meubles (parfois en très grand nombre). Ils sont de taille plutôt réduite. Chez les {{g}}gorgonocéphales{{/g}} ou astrophytons, les cinq bras sont extrêmement ramifiés. Le plus souvent, ces animaux se recroquevillent en une pelote le jour et, à la nuit tombée, ils s'épanouissent en étalant leurs bras ramifiés (à la manière des gorgones) pour capturer le plancton.", listeGroupes.get(79)));
        listeGroupes.add(new Groupe(44,0,"Holothurides","concombres de mer","gestionenligne/images_groupe/44.gif","","Les Holothurides regroupent l'ensemble des {{g}}concombres de mer{{/g}}. Ces animaux ont un épiderme généralement hérissé de piquants comme les autres Echinodermes, mais il arrive qu'ils soient lisses et ressemblent à des vers (synaptes). Les concombres de mer sont détritivores, parfois fouisseurs et ne laissent alors dépasser du substrat ou de l'anfractuosité qu'un panache tentaculaire ramifié. Dérangées, certaines espèces peuvent expulser violemment certaines structures anatomiques hors de leur corps pour leur défense. En Asie, des concombres sont consommés (trépang).", listeGroupes.get(79)));
        listeGroupes.add(new Groupe(0,0,"PROCORDÉS","","gestionenligne/images_groupe/45.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(45,0,"Tuniciers et Céphalocordés","Ascidies...","gestionenligne/images_groupe/45.gif","","Les {{g}}Tuniciers{{/g}} ou Urochordés sont contrairement aux apparences des organismes très évolués. Ils possèdent, à l'état larvaire au moins, une chorde qui annonce l'axe squelettique des futurs Vertébrés. Les {{s}}Ascidies{{/s}} sont les Tuniciers fixés. Solitaires, elles possèdent deux siphons, un inhalant (ou buccal) et un exhalant (ou cloacal). Coloniales, elles présentent une fusion des siphons exhalants et chaque groupe d'Ascidies fusionnées évoque une fleur. Chez certaines formes très évoluées, une tunique commune enveloppe la colonie qui ressemble alors à une éponge.Les Tuniciers comprennent également des formes libres et pélagiques : les {{s}}Thaliacés{{/s}} regroupent les salpes, organismes étranges en forme de tonneau flottant entre deux eaux (libres ou enchaînés les uns aux autres), les pyrosomes et les dolioles. Chez les {{s}}Appendiculaires{{/s}} enfin, qui ressemblent à de minuscules têtards, la chorde embryonnaire perdure chez l'adulte.{{n/}}{{n/}}Les {{g}}Céphalochordés{{/g}} ne sont représentés que par les amphioxus, organismes benthiques fouisseurs à symétrie bilatérale pourvus d'une chorde pérenne, et qui ressemblent à de petits poissons sans yeux. Tous ces organismes sont exclusivement marins.", listeGroupes.get(87)));
        listeGroupes.add(new Groupe(45,4,"Ascidies simples et sociales","","gestionenligne/images_sousgroupe/4.gif","","Nous regroupons ici les Ascidies dont les zoïdes sont bien visibles et individualisés. Les Ascidies simples sont solitaires ou grégaires, les Ascidies sociales forment souvent des bouquets d'individus reliés entre eux à leur base, ou grâce à un stolon qui adhère au substrat.{{n/}}[NB : ce regroupement est purement pratique et ne représente pas forcément un taxon homogène.]", listeGroupes.get(88)));
        listeGroupes.add(new Groupe(45,9,"Ascidies composées","","gestionenligne/images_sousgroupe/9.gif","","Nous regroupons ici les Ascidies dites composées, ou encore coloniales, c'est-à-dire plus ou moins fusionnées. Les zoïdes ne sont pratiquement plus discernables. On parle aussi, pour certaines d'entre elles, de {{s}}synascidies{{/s}}.{{n/}}[NB : ce regroupement est purement pratique et ne représente pas forcément un taxon homogène.]", listeGroupes.get(88)));
        listeGroupes.add(new Groupe(45,12,"Thaliacés et Appendiculaires","","gestionenligne/images_sousgroupe/12.gif","","Tous pélagiques, ces organismes transparents mystérieux et souvent spectaculaires dérivent au gré des courants.", listeGroupes.get(88)));
        listeGroupes.add(new Groupe(45,13,"Céphalocordés","","gestionenligne/images_sousgroupe/13.gif","","Ces organismes, aussi appelés amphioxus, partagent avec les Tuniciers et les Vertébrés une chorde mésodermique. Ils ne sont représentés que par quelques espèces marines enfouies sous la surface du sédiment.", listeGroupes.get(88)));
        listeGroupes.add(new Groupe(0,0,"VERTEBRES","","gestionenligne/images_groupe/46.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(46,0,"Poissons cartilagineux","requins, raies...","gestionenligne/images_groupe/46.gif","","Les Sélaciens, ou Chondrichthyens, regroupent l'ensemble des poissons à squelette cartilagineux, c'est-à-dire les requins, les raies, et les chimères.", listeGroupes.get(93)));
        listeGroupes.add(new Groupe(46,60,"Requins","","gestionenligne/images_sousgroupe/60.gif","","Les requins présentent des fentes branchiales sur le côté du corps (Pleurotrèmes).{{n/}}La distinction avec les raies est une simplification qui ne répond plus aux critères scientifiques actuels.", listeGroupes.get(94)));
        listeGroupes.add(new Groupe(46,59,"Raies","","gestionenligne/images_sousgroupe/59.gif","","Les raies présentent des fentes branchiales en dessous du corps (Hypotrèmes).{{n/}}La distinction avec les requins est une simplification qui ne répond plus aux critères scientifiques actuels.", listeGroupes.get(94)));
        listeGroupes.add(new Groupe(47,0,"Poissons osseux nageant en pleine eau","","gestionenligne/images_groupe/47.gif","","Les Ostéichthyens ou poissons osseux possèdent des nageoires soutenues par de longs rayons. {{n/}}Ceux qui vivent en pleine eau, plutôt loin du fond, sont qualifiés de pélagiques.", listeGroupes.get(93)));
        listeGroupes.add(new Groupe(47,112,"Carangues","","gestionenligne/images_sousgroupe/112.jpg","","Les {{g}}Carangidés{{/g}} (carangues, sérioles, liches...) possèdent un corps assez comprimé, souvent de teinte argentée, deux nageoires dorsales, et une queue fourchue. Fréquemment une rangée d'écailles renforcées de chaque côté du pédoncule caudal.", listeGroupes.get(97)));
        listeGroupes.add(new Groupe(47,107,"Mulets, bars, loups","","gestionenligne/images_sousgroupe/107.gif","","Famille des {{g}}Mugilidés{{/g}}, tête large et plate, petite bouche, corps allongé, deux nageoires dorsales bien séparées et assez courtes, lignes longitudinales fréquentes. Côtières, les différentes espèces de mulets sont difficiles à reconnaître.{{n/}}Les {{g}}Moronidés{{/g}} (loups, bars...) présentent un corps élancé, de teinte argentée, plus sombre en partie dorsale. Les deux dorsales sont bien séparées et leur caudale n'est que peu échancrée. Leurs lèvres sont charnues.", listeGroupes.get(97)));
        listeGroupes.add(new Groupe(47,113,"Thons, thazards, maquereaux, barracudas","","gestionenligne/images_sousgroupe/113.jpg","","Les {{g}}Scombridés{{/g}} (thons, maquereaux, thazards ...) possèdent un corps fusiforme, de section presque cylindrique, souvent argenté, avec parfois des motifs en forme de lignes longitudinales ou de barres transversales. Présence de deux nageoires dorsales, et de petites nageoires, les pinnules, alignées entre la deuxième dorsale et la caudale, et entre l'anale et la caudale, qui est fourchue.Les {{g}}Sphyraénidés{{/g}} (Barracudas, bécunes...) possèdent un corps très allongé, surmonté de deux dorsales très distinctes et terminé par une caudale fourchue. La tête est pointue, munie d'une mâchoire inférieure proéminente. Dents fortes et pointues souvent visibles.", listeGroupes.get(97)));
        listeGroupes.add(new Groupe(47,115,"Fusiliers","","gestionenligne/images_sousgroupe/115.jpg","","Familles des {{g}}Caesionidés{{/g}}", listeGroupes.get(97)));
        listeGroupes.add(new Groupe(47,114,"Autres poissons de pleine eau","","gestionenligne/images_sousgroupe/114.gif","","Nous regroupons ici les poissons vivant en pleine eau, non rattachés à un des sous-groupes.", listeGroupes.get(97)));
        listeGroupes.add(new Groupe(48,0,"Poissons osseux nageant près du fond","","gestionenligne/images_groupe/48.gif","","Les poissons osseux, ou Ostéichthyens, possèdent des nageoires soutenues par de longs rayons. {{n/}}Nous regroupons ici les espèces qui habituellement nagent à proximité du fond.", listeGroupes.get(93)));
        listeGroupes.add(new Groupe(48,104,"Truites, saumons, ombles","","gestionenligne/images_sousgroupe/104.jpg","","Famille des {{g}}Salmonidés{{/g}}, vivant en eau douce ou en eau de mer.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,84,"Morues, mostelles, merlans, lieus, tacauds,...","","gestionenligne/images_sousgroupe/84.gif","","Les poissons de l'ordre des {{g}}Gadiformes{{/g}} n'ont pas ou rarement de rayons épineux. Leurs nageoires pelviennes sont thoraciques ou jugulaires (au-dessus ou en avant des pectorales). Ils présentent souvent un barbillon mentonnier. Ce groupe inclut les motelles, les mostelles, les tacauds, les morues, les aiglefins (haddocks), les merlans et les lieus.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,87,"Poissons-soleils, gros-yeux, soldats et écureuils","","gestionenligne/images_sousgroupe/87.gif","","Les {{g}}Holocentridés{{/g}} et les {{g}}Priacanthidés{{/g}} sont des poissons amateurs d'ombre souvent de couleur rouge ou tirant sur le rouge, avec de grands yeux noirs. De jour, on les trouve souvent en banc, sous les surplombs et dans les grottes.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,53,"Mérous, serrans, cerniers, barbiers ,...","","gestionenligne/images_sousgroupe/53.gif","","{{g}}Epinéphélinés{{/g}}, {{g}}Serraninés{{/g}}, {{g}}Polyprionidés, Anthiinés{{/g}}.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,105,"Rascasses, poissons-scorpions, poissons-pierre","","gestionenligne/images_sousgroupe/105.gif","","Ordre des {{g}}Scorpaeniformes.{{/g}}", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,94,"Apogons ou cardinaux","","gestionenligne/images_sousgroupe/94.jpg","","Famille des {{g}}Apogonidés{{/g}}.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,92,"Lutjans et gaterins","","gestionenligne/images_sousgroupe/92.jpg","","Famille des {{g}}Haemulidés{{/g}} : gaterins, diagrammes, gorettes, et famille des {{g}}Lutjanidés{{/g}} : pagres, sardes, vivaneaux.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,56,"Sars, dorades, oblades, dentés, marbrés","","gestionenligne/images_sousgroupe/56.gif","","Les {{g}}Sparidés{{/g}} ont une seule nageoire dorsale, un corps ovale et comprimé, et une nageoire caudale fourchue.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,57,"Corbs, ombrines, maigres, poissons-chevaliers,...","","gestionenligne/images_sousgroupe/57.gif","","La famille des {{g}}Sciénidés{{/g}} est caractérisée par un corps haut, un dos fortement incurvé et un ventre plat. La bouche est petite, basse et presque horizontale. La mâchoire inférieure, avec ou sans barbillon, ne dépasse jamais la mâchoire supérieure.{{n/}}Les deux nageoires dorsales sont bien développées, particulièrement chez les juvéniles.{{n/}}La coloration du corps est foncée (du grec [scia] = ombre !).", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,102,"Becs de cane, empereurs ...","","gestionenligne/images_sousgroupe/102.gif","","Les {{g}}Léthrinidés{{/g}} ressemblent aux lutjans. Ils ont des lèvres épaisses, un museau souvent triangulaire, de grandes écailles et une nageoire dorsale continue.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,51,"Poissons-papillons","","gestionenligne/images_sousgroupe/51.gif","","{{g}}Chaetodontidés{{/g}} : poissons ronds à ovales, à bouche protractile, à dents fines en brosse, une seule nageoire dorsale continue sur tout le dos, écailles {{D:cténoïdes}}cténoïdes{{/D}}.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,109,"Poissons-cochers et Zancles","","gestionenligne/images_sousgroupe/109.jpg","","Les {{g}}Zanclidés{{/g}} et certains {{g}}Chaetodontidés{{/g}} de la famille des {{i}}Heniochus{{/i}} ont une aspect assez semblable : corps très aplati latéralement, bouche allongée et la nageoire dorsale prolongée en un long fouet, d'où le nom vernaculaire de poissons-cochers.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,58,"Poissons-anges","","gestionenligne/images_sousgroupe/58.gif","","Famille des{{g}} Pomacanthidés{{/g}}.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,103,"Platax","","gestionenligne/images_sousgroupe/103.gif","","Les poissons de la famille des {{g}}Ephippidés{{/g}} sont aplatis latéralement en disque et possèdent des nageoires dorsale et anale hautes. Ils comprennent les platax au sens large.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,61,"Poissons-clowns","","gestionenligne/images_sousgroupe/61.gif","","Famille des{{g}} Pomacentridés{{/g}} : poissons-clowns, ou poissons-anémones, demoiselles. 27 espèces (1 {{i}}Premnas{{/i}} et 26 {{i}}Amphiprion{{/i}}).", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,91,"Demoiselles, castagnoles, sergents-majors...","","gestionenligne/images_sousgroupe/91.gif","","Poissons de la famille des {{g}}Pomacentridés{{/g}} (demoiselles, castagnoles, sergents-majors, chromis...)", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,55,"Labres, girelles, napoléons,...","","gestionenligne/images_sousgroupe/55.gif","","Les {{g}}Labridés{{/g}} sont des poissons aux livrées colorées. La taille va de quelques cm à 1 m (Napoléon).{{n/}}Tous marins, ils habitent les mers chaudes, les eaux tempérées et très rarement les eaux froides (Océan Atlantique, O. Indien, O. Pacifique et Méditerranée).{{n/}}Ils sont tous carnivores : petits animaux planctoniques, petits poissons, crustacés, mollusques, échinodermes, parasites (labres nettoyeurs).", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,117,"Blennies et gobies nageurs","","gestionenligne/images_sousgroupe/117.jpg","","", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,93,"Perroquets","","gestionenligne/images_sousgroupe/93.jpg","","Famille des {{g}}Scaridés{{/g}}.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,83,"Poissons-chirurgiens, nasons","","gestionenligne/images_sousgroupe/83.gif","","", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,69,"Balistes","","gestionenligne/images_sousgroupe/69.gif","","Chez les {{g}}Balistidés{{/g}},{{i}} {{/i}}la première épine de la première nageoire dorsale peut se bloquer en position verticale.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,52,"Bourses ou poissons-limes","","gestionenligne/images_sousgroupe/52.gif","","La famille des {{g}}Monacanthidés{{/g}} est très proche de celle des Balistidés mais comprend des espèces au corps plus comprimé et muni d'une ou deux épines dorsales (il y en a 3 chez les balistes).", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,98,"Poissons-coffres,...","","gestionenligne/images_sousgroupe/98.gif","","", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,99,"Poissons-lapins,...","","gestionenligne/images_sousgroupe/99.gif","","", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,54,"Poissons-ballons, poissons-globes, fugu,...","","gestionenligne/images_sousgroupe/54.gif","","{{g}}Tetraodontidés{{/g}} (2 sous-familles {{g}}Canthigastérinés{{/g}} et {{g}}Tetraodontinés{{/g}}) : les dents au nombre de 4, d'où le nom de cette famille, sont soudées. Les yeux sont assez mobiles et les déplacements latéraux possibles. Ils possèdent une nageoire dorsale et une nageoire anale en position symétrique. Ces nageoires sont courtes. Les représentants de cette famille ont la capacité de gonfler, ils n'ont pas de piquants, ce qui les distingue des {{g}}Diodon{{/g}} qui eux en possèdent.{{n/}}De forme plutôt ronde, la bouche est en avant au bout d'un museau.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,106,"Poissons-trompettes, poissons-flûtes,...","","gestionenligne/images_sousgroupe/106.gif","","Les{{g}} Fistulariidés{{/g}} (poissons-flûtes) et les {{g}}Aulostomidés{{/g}} (poissons-trompettes) sont des poissons fins, allongés. Tous ont en commun des nageoires réduites et des mâchoires fusionnées (syn-gnath) en un tube qui fait office de pipette aspirante.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,108,"Poissons-fantômes, solénostomes,...","","gestionenligne/images_sousgroupe/108.gif","","Les{{g}} Solénostomidés{{/g}} (poissons-fantômes, solénostomes) sont des poissons fins, allongés, munis de divers appendices de camouflages et plus ou moins recourbés. Tous ont en commun des mâchoires fusionnées (syn-gnath) en un tube qui fait office de pipette aspirante.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(48,96,"Autres poissons près du fond","","gestionenligne/images_sousgroupe/96.gif","","Nous regroupons ici les poissons vivant près du fond qui ne sont pas rattachés à un sous-groupe.", listeGroupes.get(103)));
        listeGroupes.add(new Groupe(49,0,"« Agnathes » et Poissons osseux posés sur le fond","","gestionenligne/images_groupe/49.gif","","Les poissons osseux, ou Ostéichthyens, possèdent des nageoires soutenues par de longs rayons. Ceux qui habituellement vivent posés sur le fond, parfois également enfouis dans le sédiment ou cachés dans des anfractuosités, sont qualifiés de benthiques.Nous avons choisi de regrouper ici d'autres organismes proches des « poissons » : les lamproies et les myxines, autrefois réunies en « Agnathes », c'est-à-dire des Vertébrés sans mâchoires. Il s'agit d'espèces parasites, capable de se fixer sur des Vertébrés (poissons ou Mammifères) évoluant aussi bien sur le fond qu'en pleine eau.", listeGroupes.get(93)));
        listeGroupes.add(new Groupe(49,74,"Agnathes","","gestionenligne/images_sousgroupe/74.gif","","Les Agnathes (terme n'ayant plus de valeur systématique aujourd'hui) sont des vertébrés aquatiques parasites et sans mâchoires. Ils incluent les lamproies et les myxines.", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,88,"Chondrostéens","esturgeons...","gestionenligne/images_sousgroupe/88.gif","","Le squelette des Chondrostéens est à la fois cartilagineux (chondr-) et osseux (-ostéens). Ces poissons ancestraux, aujourd'hui en voie d'extinction et protégés pour la plupart, sont caractérisés par une peau couverte de plaques ou de tubercules osseux. On peut les trouver aussi bien en mer qu'en eau douce (cycle potamotoque). Ils comprennent les {{g}}esturgeons{{/g}} (dont on extrait le caviar) et les {{g}}poissons-spatule{{/g}}. Le beluga ({{i}}Huso huso{{/i}}) est la plus grande espèce de poisson d'eau douce du monde.", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,63,"Poissons au corps serpentiforme","","gestionenligne/images_sousgroupe/63.gif","","Nous regroupons ici des poissons appartenant à des familles éloignées, mais qui ont en commun un corps serpentiforme : congres, murènes, serpentons, serpentines, donzelles, gonnelles, anguilles, aurins,...", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,67,"Baudroies, antennaires, poissons chauve-souris,...","","gestionenligne/images_sousgroupe/67.gif","","Ordre des{{i}} {{/i}}{{g}}Lophiiformes{{/g}}.", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,62,"Hippocampes, syngnathes...","","gestionenligne/images_sousgroupe/62.gif","","Les{{g}} Syngnathiformes{{/g}} sont des poissons fins, allongés (syngnathes, entélures, solénostomes, nérophis,...) et parfois recourbés secondairement (hippocampes,...). Tous ont en commun des nageoires réduites et des mâchoires fusionnées (syn-gnath) en un tube qui fait office de pipette aspirante. Citons également les poissons-flûtes, les poissons-trompettes...", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,68,"Rascasses, poissons-scorpions, poissons-pierre","","gestionenligne/images_sousgroupe/68.gif","","Ordre des {{g}}Scorpaeniformes.{{/g}}", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,66,"Rougets, barbets, barbarins,...","","gestionenligne/images_sousgroupe/66.gif","","", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,85,"Grondins","","gestionenligne/images_sousgroupe/85.gif","","Les poissons de la famille des {{g}}Triglidés{{/g}} ont une tête massive, avec un museau prononcé, et aplatie par-dessous, munie de plaques osseuses formant une cuirasse, des épines au niveau des pièces operculaires, et un corps diminuant de taille régulièrement jusqu'à la queue. Les trois premiers rayons de la pectorale sont libres et leur permettent de fouiller le sable à la recherche de nourriture.", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,89,"Poissons-faucons","","gestionenligne/images_sousgroupe/89.gif","","Famille des {{g}}Cirrhitidés{{/g}} : ils portent typiquement de petites touffes de cirres sur les rayons de leur nageoire dorsale. Ils ont l'habitude de se tenir en embuscade, perchés sur un relief, et de fondre subitement sur leur proie d'où le nom de poissons-faucons.", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,95,"Poissons-lézards","","gestionenligne/images_sousgroupe/95.gif","","", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,64,"Blennies","","gestionenligne/images_sousgroupe/64.gif","","Les blennies (famille des {{g}}Blenniidés{{/g}}) sont des poissons benthiques. Mauvais nageurs, ils se déplacent en rampant dans les failles rocheuses. Ils ne possèdent qu'une seule nageoire dorsale, et portent souvent, au dessus des yeux, deux petites touffes caractéristiques.", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,65,"Gobies","","gestionenligne/images_sousgroupe/65.gif","","Les gobies (famille des {{g}}Gobiidés{{/g}}) sont des poissons discrets mais extrêment répandus sur fonds meubles et parmi les failles rocheuses. Mauvais nageurs (sauf le gobie nageur, {{i}}Gobiusculus flavescens{{/i}}), ils se déplacent en rampant. Ils ont toujours deux nageoires dorsales.", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,86,"Triptérygions","","gestionenligne/images_sousgroupe/86.gif","","Les {{g}}Triptérygiidés{{/g}}, comme leur nom l'indique, possèdent trois nageoires dorsales. On les trouvera toujours sur fonds rocheux, sous les surplombs et dans les anfractuosités.", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,49,"Poissons plats","","gestionenligne/images_sousgroupe/49.gif","","Les poissons plats ou {{g}}Pleuronectiformes{{/g}} possèdent les deux yeux sur une seule face. Leur corps est comprimé et couché sur un flanc dépigmenté et aveugle. Ils vivent, pour la plupart, sur ou sous les sédiments meubles (sable...).", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(49,97,"Autres poissons sur le fond","","gestionenligne/images_sousgroupe/97.gif","","Nous regroupons ici les espèces de poissons vivant sur le fond, qui ne sont pas rattachées à un sous-groupe.", listeGroupes.get(132)));
        listeGroupes.add(new Groupe(50,0,"Amphibiens","","gestionenligne/images_groupe/50.gif","","Les Amphibiens sont des Vertébrés quasi-exclusivement dulcicoles. Ils sont représentés par les {{s}}Anoures{{/s}} sans queue (grenouilles, crapauds, rainettes…) et les {{s}}Urodèles{{/s}} pourvus d'une queue (salamandres, tritons…). Les Amphibiens vivent sur terre dans des milieux humides et viennent se reproduire quasi obligatoirement dans l'eau. Certaines espèces tropicales apodes ressemblent à des serpents ({{s}}Gymnophiones{{/s}}).", listeGroupes.get(93)));
        listeGroupes.add(new Groupe(50,100,"Anoures","","gestionenligne/images_sousgroupe/100.gif","","Les Anoures regroupent les amphibiens dépourvus de queue. Ce sont les grenouilles, les crapauds, et les rainettes.", listeGroupes.get(148)));
        listeGroupes.add(new Groupe(50,101,"Urodèles","","gestionenligne/images_sousgroupe/101.gif","","Les Urodèles regroupent les amphibiens pourvus d'une queue : ce sont, entre autres, les tritons et les salamandres.", listeGroupes.get(148)));
        listeGroupes.add(new Groupe(51,0,"« Reptiles » et Mammifères aquatiques","","gestionenligne/images_groupe/51.gif","","On nomme « Reptiles » (terme n'ayant plus de valeur systématique) des Vertébrés à température variable, tétrapodes (sauf les serpents), à respiration pulmonaire, pondant des œufs et ayant le corps recouvert d'écailles mortes épidermiques. Les Reptiles aquatiques sont représentés essentiellement par les {{s}}tortues{{/s}} et quelques espèces de {{s}}serpents{{/s}}. Certains lézards (varans, dragons, iguanes) et certains crocodiles s'immergent de temps à autres pour se nourrir.{{n/}}De nombreuses espèces de Mammifères se sont complètement affranchies du milieu terrestre. Même si les {{s}}Pinnipèdes{{/s}} (phoques, morses, otaries, etc…) en sont encore dépendants, les {{s}}Siréniens{{/s}} (lamantins et dugongs) et les {{s}}Cétacés{{/s}} (dauphins, baleines…), quant à eux, présentent de prodigieuses adaptations à la vie en immersion. En eau douce, on pourra occasionnellement observer certains {{s}}Rongeurs{{/s}} (rats, loutres, castors...)", listeGroupes.get(93)));
        listeGroupes.add(new Groupe(51,17,"Tortues aquatiques","","gestionenligne/images_sousgroupe/17.gif","","Les tortues, ou {{g}}Chéloniens{{/g}}, sont aisément reconnaissables à la carapace qui enferme leur corps. Celle-ci est composée d'une partie ventrale, le plastron, et d'une partie dorsale, la dossière. Cette carapace est très solide, kératinisée. Chez quelques espèces, elle est molle. Certaines tortues peuvent rétracter leurs membres à l'intérieur de la carapace, d'autres en sont incapables. De nombreuses espèces sont aquatiques (marines ou dulcicoles), certaines sont terrestres.", listeGroupes.get(151)));
        listeGroupes.add(new Groupe(51,18,"Serpents aquatiques","","gestionenligne/images_sousgroupe/18.gif","","", listeGroupes.get(151)));
        listeGroupes.add(new Groupe(51,19,"Pinnipèdes","phoques, morses, otaries...","gestionenligne/images_sousgroupe/19.gif","","", listeGroupes.get(151)));
        listeGroupes.add(new Groupe(51,20,"Siréniens","lamantins et dugongs","gestionenligne/images_sousgroupe/20.gif","","", listeGroupes.get(151)));
        listeGroupes.add(new Groupe(51,21,"Cétacés","dauphins, baleines...","gestionenligne/images_sousgroupe/21.gif","","", listeGroupes.get(151)));
        listeGroupes.add(new Groupe(51,111,"Mammifères d'eau douce","Castors, loutres,...","gestionenligne/images_sousgroupe/111.gif","","En eau douce, on pourra occasionnellement observer certains {{s}}Rongeurs{{/s}} (rats, loutres, castors...).", listeGroupes.get(151)));
        listeGroupes.add(new Groupe(51,80,"Autres mammifères","Ours,...","gestionenligne/images_sousgroupe/80.gif","","", listeGroupes.get(151)));
        listeGroupes.add(new Groupe(52,0,"Oiseaux","","gestionenligne/images_groupe/52.gif","","Les Oiseaux ne sont pas des organismes marins sensu stricto. Certains vivent de la mer et n'y font des incursions que dans le but de se nourrir. Même si certaines espèces comme l'océanite, le fulmar ou l'albatros ne touchent terre que pour pondre, la grande majorité de ces Oiseaux fréquente le milieu côtier. Certaines espèces présentent des adaptations prodigieuses aux contraintes de la vie aquatique (manchots, pingouins, etc…). On inclura également dans ce groupe les Oiseaux qui fréquentent les lacs, étangs et rivières.{{n/}}{{i}}{{g}}Ces fiches partiellement rédigées pour la plupart sont destinées à être complétées progressivement.{{n/}}Compte tenu de l'ampleur du travail pour développer le site DORIS nous avons choisi de mettre la priorité sur les espèces sous-marines.{{n/}}En attendant nous invitons le lecteur à se diriger vers le site de référence de DORIS pour les Oiseaux : {{A:www.oiseaux.net/}}oiseaux.net{{/A}}{{/g}}{{/i}}", listeGroupes.get(93)));
        listeGroupes.add(new Groupe(0,0,"AUTRES","","gestionenligne/images_groupe/53.gif","","", listeGroupes.get(16)));
        listeGroupes.add(new Groupe(53,0,"Autres groupes mineurs","","gestionenligne/images_groupe/53.gif","","Sont regroupés ici des organismes qui ne peuvent pas être classés dans un des 52 groupes précédents. Ce seront essentiellement des organismes rares ou de très petite taille. Citons par exemple les {{g}}Entoproctes{{/g}} ou {{g}}Kamptozoaires{{/g}}, les {{g}}Rotifères{{/g}}, les {{g}}Chaetognathes{{/g}}, les {{g}}Tardigrades{{/g}}, les {{g}}Ptérobranches{{/g}}, etc., ainsi que certains animaux inclassables !", listeGroupes.get(160)));
        listeGroupes.add(new Groupe(53,118,"Kamptozoaires ou Entoproctes","","gestionenligne/images_sousgroupe/118.gif","","L'Embranchement des Kamptozoaires ou Entoproctes (Kamptozoa = Entoprocta) regroupe quelques dizaines d'espèces divisées en deux Ordres ; les Coloniales (genres : {{i}}Barentsia, Urnatella, Pedicellina, Loxosomatoides{{/i}}) et les Solitaria (genres : {{i}}Loxokalypus, Loxomespilon, Loxomitra, Loxosoma, Loxosomella{{/i}}).{{n/}}Ces organismes sont essentiellement marins, parfois d'eau saumâtre et très rarement d'eau douce. Ils peuvent atteindre une taille supérieure (3 ou 4 mm) à celle des Bryozoaires ou Ectoproctes.", listeGroupes.get(161)));


        try {
            TransactionManager.callInTransaction(connectionSource,
                    new Callable<Void>() {
                        public Void call() throws Exception {
                            for (Groupe groupe : listeGroupes){
                                dbContext.groupeDao.create(groupe);
                            }
                            return null;
                        }
                    });
        } catch ( Exception e) {
            // une erreur est survenue
            log.error("Une erreur est survenue dans PrefetchGroupes");
            log.error(e);
            return -1;
        }

        log.debug("prefetchV4() - fin");
        return 1;
    }

	public int prefetch() {
		// - - - Groupes - - -
		// Récupération de la liste des groupes sur le site de DORIS
		// En UPDATE et CDDVD on re-télécharge la liste
		
		PrefetchTools prefetchTools = new PrefetchTools();	
		SiteDoris siteDoris = new SiteDoris();
		
		String listeGroupesFichier = "";
		String contenuFichierHtml = null;
			
		try {
			
			if ( action != ActionKind.NODWNLD ){
				listeGroupesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/listeGroupes.html";
				log.info("Récup. Liste Groupes Doris : " + listeGroupesFichier);
				
				if (prefetchTools.getFichierFromUrl(Constants.getGroupesZoneUrl(Constants.getNumZoneForUrl(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES)), listeGroupesFichier)) {
					contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeGroupesFichier), FileHtmlKind.LISTE_GROUPES);
					
				} else {
					log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
					System.exit(1);
				}
			} else {
				// NODWNLD
				listeGroupesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/listeGroupes.html";
				if (new File(listeGroupesFichier).exists()) {
					contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeGroupesFichier), FileHtmlKind.LISTE_GROUPES);
				} else {
					log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
					System.exit(1);
				}
			}
			
			listeGroupes = siteDoris.getListeGroupesFromHtml(contenuFichierHtml);
			log.debug("doMain() - listeGroupes.size : "+listeGroupes.size());
			
			TransactionManager.callInTransaction(connectionSource,
				new Callable<Void>() {
					public Void call() throws Exception {
						for (Groupe groupe : listeGroupes){
							dbContext.groupeDao.create(groupe);
						}
						return null;
				    }
				});

			for (Groupe groupe : listeGroupes){
				log.info("Groupe : " + groupe.getNomGroupe());
				if (groupe.getNumeroGroupe() != 0 && (nbMaxFichesATraiter == PrefetchConstants.nbMaxFichesTraiteesDef || groupe.getNumeroGroupe() <= 10) ) {
					String fichierLocalContenuGroupe = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/groupe-10-"+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe()+"-1.html";
					String fichierRefContenuGroupe = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/groupe-10-"+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe()+"-1.html";
					
					if ( action != ActionKind.NODWNLD && action != ActionKind.CDDVD_MED
							|| action == ActionKind.CDDVD_HI && action != ActionKind.UPDATE){
						
						if (prefetchTools.getFichierFromUrl(Constants.getGroupeContenuUrl(Constants.getNumZoneForUrl(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES),
								groupe.getNumeroGroupe(), groupe.getNumeroSousGroupe(), 1), fichierLocalContenuGroupe)) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierLocalContenuGroupe), FileHtmlKind.GROUPE);
						} else {
							log.error("Une erreur est survenue lors du téléchargement du groupe : "+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe());
							System.exit(1);
						}
						
					} else if (action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI
							|| action == ActionKind.UPDATE) {
						
						// UPDATE ou CDDVD
						if ( prefetchTools.isFileExistingPath( fichierRefContenuGroupe ) ) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierRefContenuGroupe), FileHtmlKind.GROUPE);
						} else if (prefetchTools.getFichierFromUrl(Constants.getGroupeContenuUrl(Constants.getNumZoneForUrl(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES),
								groupe.getNumeroGroupe(), groupe.getNumeroSousGroupe(), 1), fichierLocalContenuGroupe)) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierLocalContenuGroupe), FileHtmlKind.GROUPE);
						} else {
							log.error("Une erreur est survenue lors du téléchargement du groupe : "+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe());
							System.exit(1);
						}
						
					} else {
						// NODWNLD
						if ( prefetchTools.isFileExistingPath( fichierRefContenuGroupe ) ) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierRefContenuGroupe), FileHtmlKind.GROUPE);
						} else {
							log.error("Une erreur est survenue lors de la récupération du groupe : "+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe());
							System.exit(1);
						}
					}
										
					groupe.setContextDB(dbContext);
					groupe.descriptionDetailleeFromHtml(contenuFichierHtml);
					groupeMaj = groupe;
					TransactionManager.callInTransaction(connectionSource,
						new Callable<Void>() {
							public Void call() throws Exception {
								dbContext.groupeDao.update(groupeMaj);
								return null;
						    }
						});
					
				}
			}
			// Téléchargement des pages de Groupes
			if ( action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI ){

				List<ZoneGeographiqueKind> listZone = Arrays.asList(ZoneGeographiqueKind.values());
				for (ZoneGeographiqueKind zone : listZone ) {
					
					int zoneId = Constants.getNumZoneForUrl(zone);
					String fichierGroupes = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/groupes_zone-"+zoneId+".html";

					if (prefetchTools.getFichierFromUrl(Constants.getGroupesZoneUrl(zoneId), fichierGroupes)) {
						contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierGroupes), FileHtmlKind.GROUPES_ZONE);
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des Groupes : " + zone.toString());
						System.exit(1);
					}
					
					final List<Groupe> listeGroupesZone = siteDoris.getListeGroupesFromHtml(contenuFichierHtml);
					log.debug("doMain() - listeGroupesZone.size : "+listeGroupesZone.size());
					
					for (Groupe groupe : listeGroupesZone) {
						
						if (groupe.getNumeroGroupe() != 0  && (nbMaxFichesATraiter == PrefetchConstants.nbMaxFichesTraiteesDef || groupe.getNumeroGroupe() <= 10) ) {
							int pageCourante = 1;
							boolean testContinu = false;
							
							do {
								log.debug("doMain() - page Groupe : "+zoneId+" - "+groupe.getNumeroGroupe()+" - "+groupe.getNumeroSousGroupe()+" - "+pageCourante);

								String fichierPageGroupe = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/groupe-"+zoneId+"-"+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe()+"-"+pageCourante+".html";

								if (prefetchTools.getFichierFromUrl(Constants.getGroupeContenuUrl(zoneId, groupe.getNumeroGroupe(), groupe.getNumeroSousGroupe(), pageCourante), fichierPageGroupe)) {
									contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierPageGroupe), FileHtmlKind.GROUPE);
								} else {
									log.error("Une erreur est survenue lors de la récupération de la page des groupes : " + fichierPageGroupe);
									System.exit(1);
								}

								pageCourante ++;
								testContinu = siteDoris.getContinuerContenuGroupeFromHtml(contenuFichierHtml);
							
							} while ( testContinu );
							
						}
					}
				}
			}

			return listeGroupes.size();
			
		} catch ( Exception e) {
			// une erreur est survenue
			log.error("Une erreur est survenue dans PrefetchGroupes");
			log.error(e);
			return -1;
		}


	}
}
