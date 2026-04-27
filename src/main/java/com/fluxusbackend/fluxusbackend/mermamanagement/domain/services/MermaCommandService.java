package com.fluxusbackend.fluxusbackend.mermamanagement.domain.services;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.aggregates.Merma;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaDonableCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaDonatedCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.MarkMermaNotDonableCommand;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.commands.RegisterMermaCommand;

public interface MermaCommandService {
    Merma handle(RegisterMermaCommand command);

    Merma handle(MarkMermaDonableCommand command);

    Merma handle(MarkMermaNotDonableCommand command);

    Merma handle(MarkMermaDonatedCommand command);
}

