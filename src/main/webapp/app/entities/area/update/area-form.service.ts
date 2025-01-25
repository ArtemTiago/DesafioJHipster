import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IArea, NewArea } from '../area.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IArea for edit and NewAreaFormGroupInput for create.
 */
type AreaFormGroupInput = IArea | PartialWithRequiredKeyOf<NewArea>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IArea | NewArea> = Omit<T, 'dataCriacao' | 'dataInatividade'> & {
  dataCriacao?: string | null;
  dataInatividade?: string | null;
};

type AreaFormRawValue = FormValueOf<IArea>;

type NewAreaFormRawValue = FormValueOf<NewArea>;

type AreaFormDefaults = Pick<NewArea, 'id' | 'dataCriacao' | 'dataInatividade'>;

type AreaFormGroupContent = {
  id: FormControl<AreaFormRawValue['id'] | NewArea['id']>;
  nome: FormControl<AreaFormRawValue['nome']>;
  descricao: FormControl<AreaFormRawValue['descricao']>;
  status: FormControl<AreaFormRawValue['status']>;
  dataCriacao: FormControl<AreaFormRawValue['dataCriacao']>;
  dataInatividade: FormControl<AreaFormRawValue['dataInatividade']>;
};

export type AreaFormGroup = FormGroup<AreaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AreaFormService {
  createAreaFormGroup(area: AreaFormGroupInput = { id: null }): AreaFormGroup {
    const areaRawValue = this.convertAreaToAreaRawValue({
      ...this.getFormDefaults(),
      ...area,
    });
    return new FormGroup<AreaFormGroupContent>({
      id: new FormControl(
        { value: areaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nome: new FormControl(areaRawValue.nome, {
        validators: [Validators.required],
      }),
      descricao: new FormControl(areaRawValue.descricao),
      status: new FormControl(areaRawValue.status, {
        validators: [Validators.required],
      }),
      dataCriacao: new FormControl(areaRawValue.dataCriacao, {
        validators: [Validators.required],
      }),
      dataInatividade: new FormControl(areaRawValue.dataInatividade),
    });
  }

  getArea(form: AreaFormGroup): IArea | NewArea {
    return this.convertAreaRawValueToArea(form.getRawValue() as AreaFormRawValue | NewAreaFormRawValue);
  }

  resetForm(form: AreaFormGroup, area: AreaFormGroupInput): void {
    const areaRawValue = this.convertAreaToAreaRawValue({ ...this.getFormDefaults(), ...area });
    form.reset(
      {
        ...areaRawValue,
        id: { value: areaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AreaFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dataCriacao: currentTime,
      dataInatividade: currentTime,
    };
  }

  private convertAreaRawValueToArea(rawArea: AreaFormRawValue | NewAreaFormRawValue): IArea | NewArea {
    return {
      ...rawArea,
      dataCriacao: dayjs(rawArea.dataCriacao, DATE_TIME_FORMAT),
      dataInatividade: dayjs(rawArea.dataInatividade, DATE_TIME_FORMAT),
    };
  }

  private convertAreaToAreaRawValue(
    area: IArea | (Partial<NewArea> & AreaFormDefaults)
  ): AreaFormRawValue | PartialWithRequiredKeyOf<NewAreaFormRawValue> {
    return {
      ...area,
      dataCriacao: area.dataCriacao ? area.dataCriacao.format(DATE_TIME_FORMAT) : undefined,
      dataInatividade: area.dataInatividade ? area.dataInatividade.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
