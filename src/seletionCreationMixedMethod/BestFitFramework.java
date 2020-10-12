package seletionCreationMixedMethod;

import dataCenterEntity.*;
import operationInterface.HolderSelectionCreation;

public interface BestFitFramework extends HolderSelectionCreation {
    Holder execute(DataCenterInterface dataCenter, Holder item);
    Double fitnessFunction(DataCenterInterface dataCenter, Holder bin, Holder item);
}
