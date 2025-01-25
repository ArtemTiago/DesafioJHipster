import dayjs from 'dayjs/esm';

import { StatusCurso } from 'app/entities/enumerations/status-curso.model';

import { IArea, NewArea } from './area.model';

export const sampleWithRequiredData: IArea = {
  id: 11565,
  nome: 'Aço África',
  status: StatusCurso['ATIVO'],
  dataCriacao: dayjs('2025-01-24T17:36'),
};

export const sampleWithPartialData: IArea = {
  id: 39625,
  nome: 'Consultant',
  status: StatusCurso['ATIVO'],
  dataCriacao: dayjs('2025-01-25T14:25'),
  dataInatividade: dayjs('2025-01-25T09:02'),
};

export const sampleWithFullData: IArea = {
  id: 9706,
  nome: 'Berkshire',
  descricao: 'Buckinghamshire Chipre',
  status: StatusCurso['INATIVO'],
  dataCriacao: dayjs('2025-01-24T17:06'),
  dataInatividade: dayjs('2025-01-25T01:56'),
};

export const sampleWithNewData: NewArea = {
  nome: 'Rua',
  status: StatusCurso['ATIVO'],
  dataCriacao: dayjs('2025-01-25T08:05'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
