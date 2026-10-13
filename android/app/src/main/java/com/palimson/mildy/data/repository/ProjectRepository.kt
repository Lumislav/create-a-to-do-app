package com.palimson.mildy.data.repository

import com.palimson.mildy.data.local.ProjectDao
import com.palimson.mildy.data.local.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val recentProjects: Flow<List<ProjectEntity>> = projectDao.getRecentProjects()

    suspend fun getProjectById(id: Long): ProjectEntity? {
        return projectDao.getProjectById(id)
    }

    suspend fun createProject(name: String, toolType: String, mediaUri: String? = null, metadata: String? = null): Long {
        val entity = ProjectEntity(
            name = name,
            toolType = toolType,
            mediaUri = mediaUri,
            metadataJson = metadata,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return projectDao.insertProject(entity)
    }

    suspend fun updateProject(project: ProjectEntity) {
        projectDao.updateProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProject(project: ProjectEntity) {
        projectDao.deleteProject(project)
    }

    suspend fun deleteById(id: Long) {
        projectDao.deleteById(id)
    }
}
