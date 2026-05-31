package com.fluxusbackend.shrinkage.domain.services;

import com.fluxusbackend.shrinkage.domain.model.aggregates.Shrinkage;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonableCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageDonatedCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageInProcessCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.MarkShrinkageNotDonableCommand;
import com.fluxusbackend.shrinkage.domain.model.commands.RegisterShrinkageCommand;

public interface ShrinkageCommandService {
    Shrinkage handle(RegisterShrinkageCommand command);

    Shrinkage handle(MarkShrinkageDonableCommand command);

    Shrinkage handle(MarkShrinkageNotDonableCommand command);

    Shrinkage handle(MarkShrinkageDonatedCommand command);

    Shrinkage handle(MarkShrinkageInProcessCommand command);
}


