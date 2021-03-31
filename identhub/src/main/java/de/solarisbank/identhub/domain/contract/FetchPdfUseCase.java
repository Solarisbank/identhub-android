package de.solarisbank.identhub.domain.contract;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import de.solarisbank.identhub.data.entity.Document;
import de.solarisbank.identhub.domain.usecase.SingleUseCase;
import de.solarisbank.identhub.file.FileController;
import de.solarisbank.sdk.core.Optional;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class FetchPdfUseCase extends SingleUseCase<Document, Optional<File>> {

    private final ContractSignRepository contractSignRepository;
    private final FileController fileController;

    public FetchPdfUseCase(
            ContractSignRepository contractSignRepository,
            FileController fileController
    ) {
        this.contractSignRepository = contractSignRepository;
        this.fileController = fileController;
    }

    @NotNull
    @Override
    protected Single<Optional<File>> invoke(Document document) {
        return Single.concat(Single.just(fileController.get(document.getName())),
                contractSignRepository.fetchDocumentFile(document.getId())
                        .map(responseBody -> fileController.save(document.getName(), responseBody.body().source())))
                .filter(Optional::isPresent)
                .first(Optional.empty())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
