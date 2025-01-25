import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICurso, NewCurso } from '../curso.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICurso for edit and NewCursoFormGroupInput for create.
 */
type CursoFormGroupInput = ICurso | PartialWithRequiredKeyOf<NewCurso>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICurso | NewCurso> = Omit<T, 'dataCriacao' | 'dataInatividade'> & {
  dataCriacao?: string | null;
  dataInatividade?: string | null;
};

type CursoFormRawValue = FormValueOf<ICurso>;

type NewCursoFormRawValue = FormValueOf<NewCurso>;

type CursoFormDefaults = Pick<NewCurso, 'id' | 'dataCriacao' | 'dataInatividade'>;

type CursoFormGroupContent = {
  id: FormControl<CursoFormRawValue['id'] | NewCurso['id']>;
  nome: FormControl<CursoFormRawValue['nome']>;
  descricao: FormControl<CursoFormRawValue['descricao']>;
  status: FormControl<CursoFormRawValue['status']>;
  dataCriacao: FormControl<CursoFormRawValue['dataCriacao']>;
  dataInatividade: FormControl<CursoFormRawValue['dataInatividade']>;
  area: FormControl<CursoFormRawValue['area']>;
};

export type CursoFormGroup = FormGroup<CursoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CursoFormService {
  createCursoFormGroup(curso: CursoFormGroupInput = { id: null }): CursoFormGroup {
    const cursoRawValue = this.convertCursoToCursoRawValue({
      ...this.getFormDefaults(),
      ...curso,
    });
    return new FormGroup<CursoFormGroupContent>({
      id: new FormControl(
        { value: cursoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nome: new FormControl(cursoRawValue.nome, {
        validators: [Validators.required],
      }),
      descricao: new FormControl(cursoRawValue.descricao),
      status: new FormControl(cursoRawValue.status, {
        validators: [Validators.required],
      }),
      dataCriacao: new FormControl(cursoRawValue.dataCriacao, {
        validators: [Validators.required],
      }),
      dataInatividade: new FormControl(cursoRawValue.dataInatividade),
      area: new FormControl(cursoRawValue.area),
    });
  }

  getCurso(form: CursoFormGroup): ICurso | NewCurso {
    return this.convertCursoRawValueToCurso(form.getRawValue() as CursoFormRawValue | NewCursoFormRawValue);
  }

  resetForm(form: CursoFormGroup, curso: CursoFormGroupInput): void {
    const cursoRawValue = this.convertCursoToCursoRawValue({ ...this.getFormDefaults(), ...curso });
    form.reset(
      {
        ...cursoRawValue,
        id: { value: cursoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CursoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dataCriacao: currentTime,
      dataInatividade: currentTime,
    };
  }

  private convertCursoRawValueToCurso(rawCurso: CursoFormRawValue | NewCursoFormRawValue): ICurso | NewCurso {
    return {
      ...rawCurso,
      dataCriacao: dayjs(rawCurso.dataCriacao, DATE_TIME_FORMAT),
      dataInatividade: dayjs(rawCurso.dataInatividade, DATE_TIME_FORMAT),
    };
  }

  private convertCursoToCursoRawValue(
    curso: ICurso | (Partial<NewCurso> & CursoFormDefaults)
  ): CursoFormRawValue | PartialWithRequiredKeyOf<NewCursoFormRawValue> {
    return {
      ...curso,
      dataCriacao: curso.dataCriacao ? curso.dataCriacao.format(DATE_TIME_FORMAT) : undefined,
      dataInatividade: curso.dataInatividade ? curso.dataInatividade.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
