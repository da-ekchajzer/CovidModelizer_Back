package com.inf1.app.batch.modelisations.calculators;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.inf1.app.dto.ModelisationDTO;
import com.inf1.app.dto.SituationReelleDTO;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class CasMachineLearningCalculator implements ModelisationCalculator {

	@Override
	public ModelisationDTO calculate(List<SituationReelleDTO> situationsReellesDTO) {
		ModelisationDTO model = new ModelisationDTO();

		// Variables utiles à l'entraînement du modèle de prédiction
		int expanse = 21;

		double realValue = Double.NaN;

		Instances[] allDataSet = new Instances[expanse];

		Instances dataSet = null;
		Instances trainSet = null;
		Instances testSet = null;

		LinearRegression[] lrClassifier = new LinearRegression[expanse];

		Evaluation[] eval = new Evaluation[expanse];

		Instance predictiveData = null;

		try {
			dataSet = initDataSet(situationsReellesDTO);
			// Entraînement du modèle de régression linéaire à plusieurs variables
			for (int n = 1; n <= expanse; n++) {
				for (int i = 0; i < dataSet.size(); i++) {
					realValue = (i + n) >= dataSet.size() ? Double.NaN : dataSet.instance(i + n).value(5);
					dataSet.instance(i).setValue(dataSet.numAttributes() - 1, realValue);
				}

				dataSet.renameAttribute(dataSet.numAttributes() - 1, "nouveaux cas J+" + n);

				trainSet = dataSet.trainCV(5, 0, new Random());
				testSet = dataSet.testCV(5, 0);

				lrClassifier[n - 1] = new LinearRegression();
				lrClassifier[n - 1].setOptions(new String[] { "-R", "1" });
				lrClassifier[n - 1].buildClassifier(trainSet);

				eval[n - 1] = new Evaluation(trainSet);
				eval[n - 1].evaluateModel(lrClassifier[n - 1], testSet);

				predictiveData = dataSet.get(dataSet.size() - 1 - n);

				allDataSet[n - 1] = new Instances(dataSet);

				for (int i = 1; i < dataSet.numAttributes(); i++) {
					if (lrClassifier[n - 1].coefficients()[i] != 0.0) {
						model.getCoeff().put("PredJ+" + n + "_" + dataSet.attribute(i).name(),
								lrClassifier[n - 1].coefficients()[i]);
					}
				}
				model.getCoeff().put("PredJ+" + n + "_constante",
						lrClassifier[n - 1].coefficients()[lrClassifier[n - 1].coefficients().length - 1]);
			}
			// Prédictions
			for (int i = 0; i < expanse; i++) {
				predictiveData = allDataSet[i].instance(dataSet.size() - (2 * expanse) - 1);
				model.getValues()
						.put(LocalDate.parse(predictiveData.stringValue(0), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
								.plusDays(i + 1),
								lrClassifier[i].classifyInstance(predictiveData) < 0 ? "0"
										: String.valueOf((int) lrClassifier[i].classifyInstance(predictiveData)));
			}
			for (int i = 0; i < allDataSet.length; i++) {
				predictiveData = allDataSet[i].get(allDataSet[i].size() - 1 - expanse);
				model.getValues()
						.put(LocalDate.parse(predictiveData.stringValue(0), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
								.plusDays(i + 1),
								lrClassifier[i].classifyInstance(predictiveData) < 0 ? "0"
										: String.valueOf((int) lrClassifier[i].classifyInstance(predictiveData)));
			}
			for (int i = 0; i < expanse; i++) {
				predictiveData = allDataSet[i].lastInstance();
				model.getValues()
						.put(LocalDate.parse(predictiveData.stringValue(0), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
								.plusDays(i + 1),
								lrClassifier[i].classifyInstance(predictiveData) < 0 ? "0"
										: String.valueOf((int) lrClassifier[i].classifyInstance(predictiveData)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return model;
	}

	private Instances initDataSet(List<SituationReelleDTO> srDTO) throws ParseException {
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		atts.add(new Attribute("date", "yyyy-MM-dd"));
		atts.add(new Attribute("nouveaux_deces"));
		atts.add(new Attribute("nouveaux_gueris"));
		atts.add(new Attribute("nouveaux_decesEhpad"));
		atts.add(new Attribute("total_reanimation"));
		atts.add(new Attribute("nouveaux_casConfirmes"));
		atts.add(new Attribute("total_hospitalises"));
		atts.add(new Attribute("nouveaux_testsRealises"));
		atts.add(new Attribute("nouveaux_testsPositifs"));
		atts.add(new Attribute("nouveaux_casConfirmesEhpad"));
		atts.add(new Attribute("nouvelles_reanimations"));
		atts.add(new Attribute("nouvelles_hospitalisations"));
		atts.add(new Attribute("nouvelles_premieresInjections"));
		atts.add(new Attribute("nouveau_r0"));
		atts.add(new Attribute("nouveaux cas J+N"));

		Instances dataSet = new Instances("CasMachineLearningCalculator dataSet", atts, 0);
		dataSet.setClassIndex(atts.size() - 1);

		double[] firstInstanceValue = new double[dataSet.numAttributes()];
		firstInstanceValue[0] = dataSet.attribute("date").parseDate("2020-03-01");
		for (int j = 1; j < firstInstanceValue.length; j++) {
			firstInstanceValue[j] = Double.NaN;
		}
		dataSet.add(new DenseInstance(1.0, firstInstanceValue));

		for (int i = 0; i < srDTO.size(); i++) {
			double[] instanceValue = new double[dataSet.numAttributes()];
			instanceValue[0] = dataSet.attribute("date")
					.parseDate(srDTO.get(i).getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			instanceValue[1] = srDTO.get(i).getNouveauxDeces() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getNouveauxDeces());
			instanceValue[2] = srDTO.get(i).getNouveauxGueris() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getNouveauxGueris());
			instanceValue[3] = srDTO.get(i).getNouveauxDecesEhpad() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getNouveauxDecesEhpad());
			instanceValue[4] = srDTO.get(i).getReanimation() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getReanimation());
			instanceValue[5] = srDTO.get(i).getNouveauxCasConfirmes() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getNouveauxCasConfirmes());
			instanceValue[6] = srDTO.get(i).getHospitalises() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getHospitalises());
			instanceValue[7] = srDTO.get(i).getTestsRealises() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getTestsRealises());
			instanceValue[8] = srDTO.get(i).getTestsPositifs() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getTestsPositifs());
			instanceValue[9] = srDTO.get(i).getNouveauxCasConfirmesEhpad() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getNouveauxCasConfirmesEhpad());
			instanceValue[10] = srDTO.get(i).getNouvellesReanimations() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getNouvellesReanimations());
			instanceValue[11] = srDTO.get(i).getNouvellesHospitalisations() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getNouvellesHospitalisations());
			instanceValue[12] = srDTO.get(i).getNouvellesPremieresInjections() == null ? Double.NaN
					: Double.parseDouble(srDTO.get(i).getNouvellesPremieresInjections());
			instanceValue[13] = Double.NaN;
			for (int d = 0; d < 15; d++) {
				if (i > 15 && !(srDTO.get(i - d).getR0() == null)) {
					instanceValue[13] = Double.parseDouble(srDTO.get(i - d).getR0());
					break;
				}
			}
			instanceValue[14] = Double.NaN;
			for (int j = 1; j < instanceValue.length; j++) {
				instanceValue[j] = Double.isNaN(instanceValue[j]) ? instanceValue[j]
						: instanceValue[j] < 0.0 ? Double.NaN : instanceValue[j];
			}
			dataSet.add(new DenseInstance(1.0, instanceValue));
		}
		return dataSet;
	}
}
