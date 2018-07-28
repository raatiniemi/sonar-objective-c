/*
 * Copyright (c) 2018 Tobias Raatiniemi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.raatiniemi.sonarqube;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Optional;
import java.util.Set;

/**
 * Collect report files matching defined pattern.
 */
public interface ReportPatternFinder {
    /**
     * Find multiple report files matching pattern.
     *
     * @param pattern Pattern to use for matching report files.
     * @return Report files matching specified pattern.
     */
    @Nonnull
    Set<File> findReportsMatching(@Nonnull String pattern);

    /**
     * Find first report file matching pattern.
     *
     * @param pattern Pattern to use for matching report files.
     * @return Report file matching specified pattern.
     */
    @Nonnull
    Optional<File> findReportMatching(@Nonnull String pattern);
}
